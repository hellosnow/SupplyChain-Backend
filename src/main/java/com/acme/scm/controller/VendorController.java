package com.acme.scm.controller;

import com.acme.commons.Result;
import com.acme.scm.model.Vendor;
import com.acme.scm.service.VendorService;
import com.acme.logging.InternalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private static final InternalLogger logger = InternalLogger.getLogger(VendorController.class);

    @Autowired
    private VendorService vendorService;

    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        logger.info("GET /api/vendors - Fetching all vendors");
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/{vendorCode}")
    public ResponseEntity<Vendor> getVendorByCode(@PathVariable String vendorCode) {
        logger.info("GET /api/vendors/{} - Fetching vendor", vendorCode);
        Result<Vendor> result = vendorService.getVendorByCode(vendorCode);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        } else {
            logger.warn("Vendor not found: {}", vendorCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{vendorCode}/rating")
    public ResponseEntity<Double> getVendorRating(@PathVariable String vendorCode) {
        logger.info("GET /api/vendors/{}/rating - Fetching vendor rating", vendorCode);
        Double rating = vendorService.getVendorRatingFromExternalService(vendorCode);
        return ResponseEntity.ok(rating);
    }
}
