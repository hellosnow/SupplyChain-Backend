package com.acme.scm.service;

import com.acme.logging.InternalLogger;
import com.acme.scm.model.Inventory;
import com.acme.scm.repository.InventoryRepository;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final InternalLogger logger = InternalLogger.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    @Qualifier("inventoryAlertSender")
    private ServiceBusSenderClient inventoryAlertSender;

    @Autowired
    private ObjectMapper objectMapper;

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
                    String json = objectMapper.writeValueAsString(item);
                    inventoryAlertSender.sendMessage(new ServiceBusMessage(json));
                    logger.info("Low stock alert sent for SKU: {}", item.getSku());
                } catch (Exception e) {
                    logger.error("Failed to send inventory alert for SKU: {}", item.getSku(), e);
                }
            }
        }
    }
}
