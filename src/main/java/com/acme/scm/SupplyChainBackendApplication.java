package com.acme.scm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Supply Chain Management System - Backend API
 *
 * TECH DEBT SUMMARY:
 * - Hardcoded credentials (should use Azure Key Vault)
 * - RabbitMQ 3.6 (should migrate to Azure Service Bus with custom API)
 * - Exception-based flow control (should use Result<T> pattern)
 */
@Slf4j
@SpringBootApplication
public class SupplyChainBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainBackendApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("========================================");
        log.info("Supply Chain Backend API Started");
        log.info("========================================");
    }
}
