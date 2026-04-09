package com.acme.scm.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String vendorCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 500)
    private String address;

    @Column(precision = 5, scale = 2)
    private BigDecimal performanceScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VendorStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = VendorStatus.ACTIVE;
        }
        if (performanceScore == null) {
            performanceScore = BigDecimal.valueOf(100.00);
        }
    }

    public enum VendorStatus {
        ACTIVE,
        INACTIVE,
        BLACKLISTED
    }
}
