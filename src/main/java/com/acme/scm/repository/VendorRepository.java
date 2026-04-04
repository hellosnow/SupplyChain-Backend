package com.acme.scm.repository;

import com.acme.scm.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByVendorCode(String vendorCode);

    List<Vendor> findByStatus(Vendor.VendorStatus status);

    List<Vendor> findByNameContaining(String name);
}
