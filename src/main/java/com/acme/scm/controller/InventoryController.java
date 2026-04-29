package com.acme.scm.controller;

import com.acme.scm.model.Inventory;
import com.acme.scm.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        log.info("GET /api/inventory - Fetching all inventory");
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Inventory> getInventoryBySku(@PathVariable String sku) {
        log.info("GET /api/inventory/{} - Fetching inventory", sku);
        return ResponseEntity.ok(inventoryService.getInventoryBySku(sku));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        log.info("GET /api/inventory/low-stock - Fetching low stock items");
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
}
