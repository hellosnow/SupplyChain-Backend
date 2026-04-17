package com.acme.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * TECH DEBT:
 * - RestTemplate bean (should use ServiceMesh SDK instead)
 */
@Configuration
public class AppConfig {

    /**
     * TECH DEBT: RestTemplate bypasses the service mesh layer.
     * Should be replaced with ServiceMesh SDK (com.acme.mesh.ServiceMesh)
     * per guardrails requirements.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
