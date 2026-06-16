package com.acme.scm.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Configuration
    @Profile("azure")
    static class AzureServiceBusConfig {

        @Value("${spring.cloud.azure.servicebus.connection-string}")
        private String connectionString;

        @Value("${app.messaging.queue.order-created}")
        private String orderCreatedQueue;

        @Value("${app.messaging.queue.inventory-alert}")
        private String inventoryAlertQueue;

        @Bean
        public ServiceBusSenderClient orderCreatedSender() {
            return new ServiceBusClientBuilder()
                    .connectionString(connectionString)
                    .sender()
                    .queueName(orderCreatedQueue)
                    .buildClient();
        }

        @Bean
        public ServiceBusSenderClient inventoryAlertSender() {
            return new ServiceBusClientBuilder()
                    .connectionString(connectionString)
                    .sender()
                    .queueName(inventoryAlertQueue)
                    .buildClient();
        }
    }
}
