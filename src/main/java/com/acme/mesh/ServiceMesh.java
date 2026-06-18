package com.acme.mesh;

import org.springframework.stereotype.Component;

/**
 * ServiceMesh SDK for standardized service-to-service communication.
 * All inter-service calls must go through the mesh per playbook policy.
 * 
 * This is a stub for the internal ServiceMesh SDK.
 * In production, this will be provided by the com.acme.mesh artifact.
 */
public class ServiceMesh {

    /**
     * Call a service through the mesh.
     * @param serviceName logical name of the target service
     * @param path API path on the target service
     * @param responseType expected response type
     * @return response from the target service, or null if the call fails
     */
    public <T> T call(String serviceName, String path, Class<T> responseType) {
        // Stub implementation - the real SDK routes through the service mesh
        throw new UnsupportedOperationException(
            "ServiceMesh SDK stub: In production, this calls " + serviceName + path + " through the mesh layer");
    }
}
