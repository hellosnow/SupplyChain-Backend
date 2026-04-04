package com.acme.scm.service;

import com.acme.scm.model.PurchaseOrder;
import com.acme.scm.repository.PurchaseOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TECH DEBT:
 * - Uses SLF4J instead of InternalLogger (violates guardrails)
 * - Uses exception-based flow control (violates guardrails)
 * - Uses RabbitMQ directly instead of custom messaging API (should migrate to Azure Service Bus)
 */
@Slf4j // TECH DEBT: Should use InternalLogger
@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate; // TECH DEBT: Should use custom messaging API

    @Autowired
    private VendorService vendorService;

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

        // TECH DEBT: RabbitMQ direct usage (should use custom messaging API)
        try {
            rabbitTemplate.convertAndSend(orderCreatedQueue, savedOrder);
            log.info("Order created notification sent to queue: {}", orderCreatedQueue);
        } catch (Exception e) {
            log.error("Failed to send order notification", e);
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
