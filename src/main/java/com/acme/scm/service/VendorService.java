package com.acme.scm.service;

import com.acme.scm.model.Vendor;
import com.acme.scm.repository.VendorRepository;
import com.acme.logging.InternalLogger;
import com.acme.mesh.ServiceMesh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TECH DEBT:
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
     * Calls the vendor rating service through the ServiceMesh SDK,
     * ensuring all inter-service traffic is routed through the mesh layer
     * with Managed Identity authentication per standards.md.
     */
    public Double getVendorRatingFromExternalService(String vendorCode) {
        try {
            logger.debug("Calling external vendor rating service via ServiceMesh for vendorCode: {}", vendorCode);
            Double rating = serviceMesh.call("vendor-rating-service", "/api/ratings/" + vendorCode, Double.class);
            return rating != null ? rating : 0.0;
        } catch (Exception e) {
            logger.error("Failed to get vendor rating", e);
            return 0.0;
        }
    }
}
