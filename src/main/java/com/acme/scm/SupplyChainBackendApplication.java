package com.acme.scm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Supply Chain Management System - Backend API
 *
 * TECH DEBT SUMMARY:
 * - Java 8 (should be Java 17 LTS per guardrails)
 * - Spring Boot 2.7.18 (should be Spring Boot 3.x)
 * - RestTemplate usage (should use ServiceMesh SDK)
 * - SLF4J logging (should use InternalLogger)
 * - Hardcoded credentials (should use Azure Key Vault)
 * - RabbitMQ 3.6 (should migrate to Azure Service Bus with custom API)
 * - Exception-based flow control (should use Result<T> pattern)
 */
@SpringBootApplication
public class SupplyChainBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainBackendApplication.class, args);
        System.out.println("========================================");
        System.out.println("Supply Chain Backend API Started");
        System.out.println("API Docs: http://localhost:8080/api");
        System.out.println("========================================");
    }
}
