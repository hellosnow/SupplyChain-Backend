package com.acme.scm.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * TECH DEBT:
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
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
