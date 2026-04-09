package com.acme.scm.controller;

import com.acme.scm.model.PurchaseOrder;
import com.acme.scm.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TECH DEBT:
 * - Uses @ControllerAdvice for exception handling (should use Result<T> pattern)
 * - Uses SLF4J instead of InternalLogger
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService orderService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        log.info("GET /api/orders - Fetching all orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<PurchaseOrder> getOrderByNumber(@PathVariable String orderNumber) {
        log.info("GET /api/orders/{} - Fetching order", orderNumber);
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PurchaseOrder>> getPendingOrders() {
        log.info("GET /api/orders/pending - Fetching pending orders");
        return ResponseEntity.ok(orderService.getPendingOrders());
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@RequestBody PurchaseOrder order) {
        log.info("POST /api/orders - Creating new order: {}", order.getOrderNumber());
        PurchaseOrder created = orderService.createOrder(order);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{orderNumber}/approve")
    public ResponseEntity<PurchaseOrder> approveOrder(
            @PathVariable String orderNumber,
            @RequestParam String approvedBy) {
        log.info("PUT /api/orders/{}/approve - Approving order by {}", orderNumber, approvedBy);
        PurchaseOrder approved = orderService.approveOrder(orderNumber, approvedBy);
        return ResponseEntity.ok(approved);
    }
}
