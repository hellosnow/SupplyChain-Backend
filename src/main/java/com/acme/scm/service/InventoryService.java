package com.acme.scm.service;

import com.acme.scm.model.Inventory;
import com.acme.scm.repository.InventoryRepository;
import com.acme.logging.InternalLogger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TECH DEBT:
 * - Uses SLF4J instead of InternalLogger
 * - Uses RabbitMQ directly instead of custom messaging API
 */
@Service
public class InventoryService {

    private static final InternalLogger logger = InternalLogger.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate; // TECH DEBT: Should use custom messaging API

    @Value("${app.messaging.queue.inventory-alert}")
    private String inventoryAlertQueue;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryBySku(String sku) {
        return inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + sku)); // TECH DEBT: Exception flow
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(Inventory::isLowStock)
                .collect(Collectors.toList());
    }

    public void checkAndSendLowStockAlerts() {
        List<Inventory> lowStockItems = getLowStockItems();

        if (!lowStockItems.isEmpty()) {
            logger.warn("Found {} low stock items", lowStockItems.size());

            // TECH DEBT: RabbitMQ direct usage (should use custom messaging API)
            for (Inventory item : lowStockItems) {
                try {
                    rabbitTemplate.convertAndSend(inventoryAlertQueue, item);
                    logger.info("Low stock alert sent for SKU: {}", item.getSku());
                } catch (Exception e) {
                    logger.error("Failed to send inventory alert for SKU: {}", item.getSku(), e);
                }
            }
        }
    }
}
