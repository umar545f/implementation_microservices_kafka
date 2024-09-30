package com.umar.notificationservice.inventoryservice.service;

import com.umar.notificationservice.inventoryservice.dto.InventoryResponse;
import com.umar.notificationservice.inventoryservice.model.Inventory;
import com.umar.notificationservice.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes)
    {
        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);
        return skuCodes.stream().map(sku -> {
            Optional<Inventory> inventory = inventories.stream()
                    .filter(inv -> inv.getSkuCode().equals(sku))
                    .findFirst();
            return InventoryResponse.builder()
                    .skuCode(sku)
                    .isInStock(inventory.isPresent() && inventory.get().getQuantity() > 0)
                    .build();
        }).collect(Collectors.toList());
    }
}
