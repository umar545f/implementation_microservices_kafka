package com.umar.notificationservice.inventoryservice.repository;

import com.umar.notificationservice.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
