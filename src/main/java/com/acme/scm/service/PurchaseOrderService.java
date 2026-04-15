package com.acme.scm.service;

import com.acme.commons.Result;
import com.acme.scm.model.PurchaseOrder;
import com.acme.scm.repository.PurchaseOrderRepository;
import com.acme.logging.InternalLogger;
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
@Service
public class PurchaseOrderService {

    private static final InternalLogger logger = InternalLogger.getLogger(PurchaseOrderService.class);

    @Autowired
    private PurchaseOrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate; // TECH DEBT: Should use custom messaging API

    @Autowired
    private VendorService vendorService;

    @Value("${app.messaging.queue.order-created}")
    private String orderCreatedQueue;

    @Transactional
    public Result<PurchaseOrder> createOrder(PurchaseOrder order) {
        logger.info("Creating purchase order: {}", order.getOrderNumber());

        if (order.getTotalAmount().doubleValue() <= 0) {
            return Result.failure("Order amount must be greater than zero");
        }

        if (!vendorService.isVendorActive(order.getVendorId())) {
            return Result.failure("Vendor is not active: " + order.getVendorId());
        }

        PurchaseOrder savedOrder = orderRepository.save(order);

        // Infrastructure-level exception handling (not business logic flow control)
        try {
            rabbitTemplate.convertAndSend(orderCreatedQueue, savedOrder);
            logger.info("Order created notification sent to queue: {}", orderCreatedQueue);
        } catch (Exception e) {
            logger.error("Failed to send order notification", e);
        }

        return Result.success(savedOrder);
    }

    public List<PurchaseOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public Result<PurchaseOrder> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(Result::success)
                .orElse(Result.failure("Order not found: " + orderNumber));
    }

    public List<PurchaseOrder> getPendingOrders() {
        return orderRepository.findByStatus(PurchaseOrder.OrderStatus.PENDING);
    }

    @Transactional
    public Result<PurchaseOrder> approveOrder(String orderNumber, String approvedBy) {
        Result<PurchaseOrder> orderResult = getOrderByNumber(orderNumber);
        if (orderResult.isFailure()) {
            return orderResult;
        }
        PurchaseOrder order = orderResult.getValue();

        if (order.getStatus() != PurchaseOrder.OrderStatus.PENDING) {
            return Result.failure("Order is not in pending status");
        }

        order.setStatus(PurchaseOrder.OrderStatus.APPROVED);
        order.setApprovedBy(approvedBy);
        order.setApprovedDate(java.time.LocalDateTime.now());

        return Result.success(orderRepository.save(order));
    }
}
