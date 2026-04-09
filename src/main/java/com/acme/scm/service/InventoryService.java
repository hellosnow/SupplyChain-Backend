package com.acme.scm.service;

import com.acme.scm.model.Inventory;
import com.acme.scm.repository.InventoryRepository;
import com.azure.spring.messaging.servicebus.core.ServiceBusTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Inventory Service.
 * Low-stock alerts are sent via Azure Service Bus using ServiceBusTemplate.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    private final ServiceBusTemplate serviceBusTemplate;

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
            log.warn("Found {} low stock items", lowStockItems.size());

            for (Inventory item : lowStockItems) {
                try {
                    serviceBusTemplate.sendAsync(inventoryAlertQueue, MessageBuilder.withPayload(item).build()).block();
                    log.info("Low stock alert sent for SKU: {}", item.getSku());
                } catch (Exception e) {
                    log.error("Failed to send inventory alert for SKU: {}", item.getSku(), e);
                }
            }
        }
    }
}
