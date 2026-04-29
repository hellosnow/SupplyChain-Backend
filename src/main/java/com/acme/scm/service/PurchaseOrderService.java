package com.acme.scm.service;

import com.acme.scm.model.PurchaseOrder;
import com.acme.scm.repository.PurchaseOrderRepository;
import com.azure.spring.messaging.servicebus.core.ServiceBusTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Purchase Order Service.
 * Messaging is performed via Azure Service Bus using ServiceBusTemplate.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;

    private final ServiceBusTemplate serviceBusTemplate;

    private final VendorService vendorService;

    @Value("${app.messaging.queue.order-created}")
    private String orderCreatedQueue;

    @Transactional
    public PurchaseOrder createOrder(PurchaseOrder order) {
        log.info("Creating purchase order: {}", order.getOrderNumber());

        // TECH DEBT: Exception-based flow control (should use Result<T> pattern)
        if (order.getTotalAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }

        // TECH DEBT: Exception-based validation (should use Result<T>)
        if (!vendorService.isVendorActive(order.getVendorId())) {
            throw new IllegalStateException("Vendor is not active: " + order.getVendorId());
        }

        PurchaseOrder savedOrder = orderRepository.save(order);

        try {
            serviceBusTemplate.sendAsync(orderCreatedQueue, MessageBuilder.withPayload(savedOrder).build()).block();
            log.info("Order created notification sent to queue: {}", orderCreatedQueue);
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
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber)); // TECH DEBT: Exception flow
    }

    public List<PurchaseOrder> getPendingOrders() {
        return orderRepository.findByStatus(PurchaseOrder.OrderStatus.PENDING);
    }

    @Transactional
    public PurchaseOrder approveOrder(String orderNumber, String approvedBy) {
        PurchaseOrder order = getOrderByNumber(orderNumber);

        // TECH DEBT: Exception-based flow control
        if (order.getStatus() != PurchaseOrder.OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in pending status");
        }

        order.setStatus(PurchaseOrder.OrderStatus.APPROVED);
        order.setApprovedBy(approvedBy);
        order.setApprovedDate(java.time.LocalDateTime.now());

        return orderRepository.save(order);
    }
}
