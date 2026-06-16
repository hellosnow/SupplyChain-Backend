package com.acme.scm.service;

import com.acme.scm.model.Inventory;
import com.acme.scm.repository.InventoryRepository;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired(required = false)
    @Qualifier("inventoryAlertSender")
    private ServiceBusSenderClient inventoryAlertSender;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryBySku(String sku) {
        return inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + sku));
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
                    if (inventoryAlertSender != null) {
                        String messageBody = objectMapper.writeValueAsString(item);
                        inventoryAlertSender.sendMessage(new ServiceBusMessage(messageBody));
                        log.info("Low stock alert sent for SKU: {}", item.getSku());
                    } else {
                        log.debug("Service Bus sender not configured, skipping alert for SKU: {}", item.getSku());
                    }
                } catch (Exception e) {
                    log.error("Failed to send inventory alert for SKU: {}", item.getSku(), e);
                }
            }
        }
    }
}
