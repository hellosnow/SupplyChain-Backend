package com.acme.scm.service;

import com.acme.mesh.ServiceMesh;
import com.acme.scm.model.Vendor;
import com.acme.scm.repository.VendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ServiceMesh serviceMesh;

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Vendor getVendorByCode(String vendorCode) {
        return vendorRepository.findByVendorCode(vendorCode)
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + vendorCode));
    }

    public boolean isVendorActive(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .map(v -> v.getStatus() == Vendor.VendorStatus.ACTIVE)
                .orElse(false);
    }

    /**
     * Retrieves the vendor rating from the external vendor rating service via the ServiceMesh SDK.
     * The SDK provides service discovery, circuit breaking, mTLS termination, and distributed tracing.
     */
    public Double getVendorRatingFromExternalService(String vendorCode) {
        try {
            log.debug("Calling vendor-rating-service for vendor: {}", vendorCode);
            Double rating = serviceMesh.call("vendor-rating-service", "/api/ratings/" + vendorCode, Double.class);
            return rating != null ? rating : 0.0;
        } catch (Exception e) {
            log.error("Failed to get vendor rating for vendor: {}", vendorCode, e);
            return 0.0;
        }
    }
}
