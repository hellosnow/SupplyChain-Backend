package com.acme.scm.service;

import com.acme.logging.InternalLogger;
import com.acme.scm.model.Inventory;
import com.acme.scm.repository.InventoryRepository;
import com.azure.spring.messaging.servicebus.core.ServiceBusTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final InternalLogger logger = InternalLogger.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceBusTemplate serviceBusTemplate;

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

            for (Inventory item : lowStockItems) {
                try {
                    serviceBusTemplate.send(inventoryAlertQueue, MessageBuilder.withPayload(item).build());
                    logger.info("Low stock alert sent for SKU: {}", item.getSku());
                } catch (Exception e) {
                    logger.error("Failed to send inventory alert for SKU: {}", item.getSku(), e);
                }
            }
        }
    }
}
