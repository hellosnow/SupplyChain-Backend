package com.acme.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Application configuration.
 * Azure Service Bus messaging is auto-configured by spring-cloud-azure-starter-servicebus.
 * Credentials are managed via Azure Key Vault with Managed Identity.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
