package com.acme.scm.controller;

import com.acme.scm.model.Inventory;
import com.acme.scm.service.InventoryService;
import com.acme.logging.InternalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final InternalLogger logger = InternalLogger.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        logger.info("GET /api/inventory - Fetching all inventory");
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Inventory> getInventoryBySku(@PathVariable String sku) {
        logger.info("GET /api/inventory/{} - Fetching inventory", sku);
        return ResponseEntity.ok(inventoryService.getInventoryBySku(sku));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        logger.info("GET /api/inventory/low-stock - Fetching low stock items");
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
}
