package com.acme.scm.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * TECH DEBT:
 * - RestTemplate bean (should use ServiceMesh SDK instead)
 * - RabbitMQ configuration (should migrate to Azure Service Bus with custom messaging API)
 */
@Configuration
public class AppConfig {

    @Value("${app.messaging.queue.order-created}")
    private String orderCreatedQueue;

    @Value("${app.messaging.queue.inventory-alert}")
    private String inventoryAlertQueue;

    @Value("${app.messaging.queue.approval-pending}")
    private String approvalPendingQueue;

    /**
     * TECH DEBT: RestTemplate bypasses the service mesh layer.
     * Should be replaced with ServiceMesh SDK (com.acme.mesh.ServiceMesh)
     * per guardrails requirements.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * TECH DEBT: RabbitMQ configuration.
     * Should migrate to Azure Service Bus with custom messaging API.
     */
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueue, true);
    }

    @Bean
    public Queue inventoryAlertQueue() {
        return new Queue(inventoryAlertQueue, true);
    }

    @Bean
    public Queue approvalPendingQueue() {
        return new Queue(approvalPendingQueue, true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonJsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
