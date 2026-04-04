package com.acme.scm.repository;

import com.acme.scm.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findBySku(String sku);

    List<Inventory> findByWarehouseLocation(String warehouseLocation);

    List<Inventory> findByQuantityLessThanEqual(Integer quantity);
}
