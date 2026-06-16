package com.acme.scm.service;

import com.acme.scm.model.PurchaseOrder;
import com.acme.scm.repository.PurchaseOrderRepository;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository orderRepository;

    @Autowired(required = false)
    @Qualifier("orderCreatedSender")
    private ServiceBusSenderClient orderCreatedSender;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public PurchaseOrder createOrder(PurchaseOrder order) {
        log.info("Creating purchase order: {}", order.getOrderNumber());

        if (order.getTotalAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }

        if (!vendorService.isVendorActive(order.getVendorId())) {
            throw new IllegalStateException("Vendor is not active: " + order.getVendorId());
        }

        PurchaseOrder savedOrder = orderRepository.save(order);

        try {
            if (orderCreatedSender != null) {
                String messageBody = objectMapper.writeValueAsString(savedOrder);
                orderCreatedSender.sendMessage(new ServiceBusMessage(messageBody));
                log.info("Order created notification sent to Azure Service Bus");
            } else {
                log.debug("Service Bus sender not configured, skipping message send");
            }
        } catch (Exception e) {
            log.error("Failed to send order notification", e);
        }

        return savedOrder;
    }

    public List<PurchaseOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public PurchaseOrder getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
    }

    public List<PurchaseOrder> getPendingOrders() {
        return orderRepository.findByStatus(PurchaseOrder.OrderStatus.PENDING);
    }

    @Transactional
    public PurchaseOrder approveOrder(String orderNumber, String approvedBy) {
        PurchaseOrder order = getOrderByNumber(orderNumber);

        if (order.getStatus() != PurchaseOrder.OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in pending status");
        }

        order.setStatus(PurchaseOrder.OrderStatus.APPROVED);
        order.setApprovedBy(approvedBy);
        order.setApprovedDate(java.time.LocalDateTime.now());

        return orderRepository.save(order);
    }
}
