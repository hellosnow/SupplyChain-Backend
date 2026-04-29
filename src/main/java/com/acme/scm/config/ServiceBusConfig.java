package com.acme.scm.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBusConfig {

    @Value("${app.azure.servicebus.namespace}")
    private String namespace;

    @Value("${app.messaging.queue.order-created}")
    private String orderCreatedQueue;

    @Value("${app.messaging.queue.inventory-alert}")
    private String inventoryAlertQueue;

    @Value("${app.messaging.queue.approval-pending}")
    private String approvalPendingQueue;

    private ServiceBusClientBuilder clientBuilder() {
        return new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(namespace)
                .credential(new DefaultAzureCredentialBuilder().build());
    }

    @Bean
    public ServiceBusSenderClient orderCreatedSender() {
        return clientBuilder().sender().queueName(orderCreatedQueue).buildClient();
    }

    @Bean
    public ServiceBusSenderClient inventoryAlertSender() {
        return clientBuilder().sender().queueName(inventoryAlertQueue).buildClient();
    }

    @Bean
    public ServiceBusSenderClient approvalPendingSender() {
        return clientBuilder().sender().queueName(approvalPendingQueue).buildClient();
    }
}
