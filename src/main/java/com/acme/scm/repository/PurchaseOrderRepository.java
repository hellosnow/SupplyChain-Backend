package com.acme.scm.repository;

import com.acme.scm.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    List<PurchaseOrder> findByStatus(PurchaseOrder.OrderStatus status);

    List<PurchaseOrder> findByVendorId(Long vendorId);

    List<PurchaseOrder> findByRequestedBy(String requestedBy);
}
