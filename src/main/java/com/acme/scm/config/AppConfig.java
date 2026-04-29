package com.acme.scm.config;

import com.acme.mesh.ServiceMesh;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ServiceMesh serviceMesh() {
        return new ServiceMesh();
    }
}
