package com.acme.scm.service;

import com.acme.commons.Result;
import com.acme.logging.InternalLogger;
import com.acme.mesh.ServiceMesh;
import com.acme.scm.model.Vendor;
import com.acme.scm.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TECH DEBT:
 * - Uses RestTemplate (bypasses mesh, violates guardrails)
 * - Uses SLF4J instead of InternalLogger
 */
@Service
public class VendorService {

    private static final InternalLogger logger = InternalLogger.getLogger(VendorService.class);

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ServiceMesh serviceMesh;

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Result<Vendor> getVendorByCode(String vendorCode) {
        return vendorRepository.findByVendorCode(vendorCode)
                .map(Result::ok)
                .orElse(Result.fail("Vendor not found: " + vendorCode));
    }

    public boolean isVendorActive(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .map(v -> v.getStatus() == Vendor.VendorStatus.ACTIVE)
                .orElse(false);
    }

    /**
     * Calls the external vendor rating service through the ServiceMesh SDK.
     */
    public Result<Double> getVendorRatingFromExternalService(String vendorCode) {
        try {
            logger.debug("Calling vendor rating service through mesh for vendorCode: {}", vendorCode);

            Double rating = serviceMesh.call("vendor-rating-service", "/api/ratings/" + vendorCode, Double.class);
            return Result.ok(rating != null ? rating : 0.0);
        } catch (Exception e) {
            logger.error("Failed to get vendor rating", e);
            return Result.fail("Failed to retrieve vendor rating for: " + vendorCode);
        }
    }
}
