package com.acme.scm.service;

import com.acme.logging.InternalLogger;
import com.acme.scm.model.PurchaseOrder;
import com.acme.scm.repository.PurchaseOrderRepository;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TECH DEBT:
 * - Uses exception-based flow control (violates guardrails)
 */
@Service
public class PurchaseOrderService {

    private static final InternalLogger logger = InternalLogger.getLogger(PurchaseOrderService.class);

    @Autowired
    private PurchaseOrderRepository orderRepository;

    @Autowired
    @Qualifier("orderCreatedSender")
    private ServiceBusSenderClient orderCreatedSender;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public PurchaseOrder createOrder(PurchaseOrder order) {
        logger.info("Creating purchase order: {}", order.getOrderNumber());

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
            String json = objectMapper.writeValueAsString(savedOrder);
            orderCreatedSender.sendMessage(new ServiceBusMessage(json));
            logger.info("Order created notification sent");
        } catch (Exception e) {
            logger.error("Failed to send order notification", e);
            // TECH DEBT: Swallowing exception
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
