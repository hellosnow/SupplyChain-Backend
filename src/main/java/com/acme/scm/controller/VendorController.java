package com.acme.scm.controller;

import com.acme.scm.model.Vendor;
import com.acme.scm.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        log.info("GET /api/vendors - Fetching all vendors");
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/{vendorCode}")
    public ResponseEntity<Vendor> getVendorByCode(@PathVariable String vendorCode) {
        log.info("GET /api/vendors/{} - Fetching vendor", vendorCode);
        return ResponseEntity.ok(vendorService.getVendorByCode(vendorCode));
    }

    @GetMapping("/{vendorCode}/rating")
    public ResponseEntity<Double> getVendorRating(@PathVariable String vendorCode) {
        log.info("GET /api/vendors/{}/rating - Fetching vendor rating", vendorCode);
        Double rating = vendorService.getVendorRatingFromExternalService(vendorCode);
        return ResponseEntity.ok(rating);
    }
}
