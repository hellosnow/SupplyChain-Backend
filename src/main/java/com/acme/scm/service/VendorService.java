package com.acme.scm.service;

import com.acme.scm.model.Vendor;
import com.acme.scm.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * TECH DEBT:
 * - Exception-based flow control (should use Result<T> pattern)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;

    private final RestClient restClient;

    @Value("${app.vendor-rating-service.base-url}")
    private String vendorRatingBaseUrl;

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

    public Double getVendorRatingFromExternalService(String vendorCode) {
        try {
            String url = vendorRatingBaseUrl + "/" + vendorCode;
            log.debug("Calling external vendor rating service: {}", url);

            Double rating = restClient.get().uri(url).retrieve().body(Double.class);
            return rating != null ? rating : 0.0;
        } catch (Exception e) {
            log.error("Failed to get vendor rating", e);
            return 0.0;
        }
    }
}
