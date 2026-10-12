package com.supermarket.inventoryservice.controller;

import com.supermarket.inventoryservice.dto.InventoryDTO;
import com.supermarket.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/getinventory")
    public List<InventoryDTO> getInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/inventory/{inventoryId}")
    public InventoryDTO getInventoryById(@PathVariable Long inventoryId) {
        return inventoryService.getInventoryById(inventoryId);
    }

    @GetMapping("/inventory/branch/{branchId}/product/{productId}")
    public InventoryDTO getInventory(@PathVariable Long branchId, @PathVariable Long productId) {
        return inventoryService.getInventory(branchId, productId);
    }

    @GetMapping("/inventory/branch/{branchId}")
    public List<InventoryDTO> getInventoryByBranch(@PathVariable Long branchId) {
        return inventoryService.getInventoryByBranch(branchId);
    }

    @GetMapping("/inventory/product/{productId}")
    public List<InventoryDTO> getInventoryByProduct(@PathVariable Long productId) {
        return inventoryService.getInventoryByProduct(productId);
    }

    @PostMapping("/addinventory")
    public InventoryDTO saveInventory(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.saveInventory(inventoryDTO);
    }

    @PutMapping("/updateinventory")
    public InventoryDTO updateInventory(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.updateInventory(inventoryDTO);
    }

    @DeleteMapping("/deleteinventory/{inventoryId}")
    public String deleteInventory(@PathVariable Long inventoryId) {
        return inventoryService.deleteInventory(inventoryId);
    }

}
