package com.acme.scm.service;

import com.acme.scm.model.Vendor;
import com.acme.scm.repository.VendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * TECH DEBT:
 * - Uses RestTemplate (bypasses mesh, violates guardrails)
 * - Uses SLF4J instead of InternalLogger
 */
@Slf4j // TECH DEBT: Should use InternalLogger
@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private RestTemplate restTemplate; // TECH DEBT: Should use ServiceMesh SDK

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Vendor getVendorByCode(String vendorCode) {
        return vendorRepository.findByVendorCode(vendorCode)
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + vendorCode)); // TECH DEBT: Exception flow
    }

    public boolean isVendorActive(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .map(v -> v.getStatus() == Vendor.VendorStatus.ACTIVE)
                .orElse(false);
    }

    /**
     * TECH DEBT: This method uses RestTemplate to call an external vendor rating service.
     * Should be replaced with ServiceMesh SDK to integrate with the service mesh layer.
     */
    public Double getVendorRatingFromExternalService(String vendorCode) {
        try {
            // TECH DEBT: RestTemplate bypasses mesh layer
            String url = "http://vendor-rating-service/api/ratings/" + vendorCode;
            log.debug("Calling external vendor rating service: {}", url);

            // This simulates calling an external service
            Double rating = restTemplate.getForObject(url, Double.class);
            return rating != null ? rating : 0.0;
        } catch (Exception e) {
            log.error("Failed to get vendor rating", e);
            return 0.0; // TECH DEBT: Returning default value on error
        }
    }
}
