package com.acme.scm.controller;

import com.acme.logging.InternalLogger;
import com.acme.scm.model.PurchaseOrder;
import com.acme.scm.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TECH DEBT:
 * - Uses @ControllerAdvice for exception handling (should use Result<T> pattern)
 * - Uses SLF4J instead of InternalLogger
 */
@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {

    private static final InternalLogger logger = InternalLogger.getLogger(PurchaseOrderController.class);

    @Autowired
    private PurchaseOrderService orderService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        logger.info("GET /api/orders - Fetching all orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<PurchaseOrder> getOrderByNumber(@PathVariable String orderNumber) {
        logger.info("GET /api/orders/{} - Fetching order", orderNumber);
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PurchaseOrder>> getPendingOrders() {
        logger.info("GET /api/orders/pending - Fetching pending orders");
        return ResponseEntity.ok(orderService.getPendingOrders());
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@RequestBody PurchaseOrder order) {
        logger.info("POST /api/orders - Creating new order: {}", order.getOrderNumber());
        PurchaseOrder created = orderService.createOrder(order);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{orderNumber}/approve")
    public ResponseEntity<PurchaseOrder> approveOrder(
            @PathVariable String orderNumber,
            @RequestParam String approvedBy) {
        logger.info("PUT /api/orders/{}/approve - Approving order by {}", orderNumber, approvedBy);
        PurchaseOrder approved = orderService.approveOrder(orderNumber, approvedBy);
        return ResponseEntity.ok(approved);
    }
}
