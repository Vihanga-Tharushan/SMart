package com.supermarket.inventoryservice.service;

import com.supermarket.inventoryservice.dto.InventoryDTO;
import com.supermarket.inventoryservice.model.Inventory;
import com.supermarket.inventoryservice.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<InventoryDTO> getAllInventory() {
        return modelMapper.map(inventoryRepository.findAll(), new TypeToken<List<InventoryDTO>>() {}.getType());
    }

    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id).orElse(null);
        return modelMapper.map(inventory, InventoryDTO.class);
    }

    public InventoryDTO getInventory(Long branchId, Long productId) {
        Inventory inventory = inventoryRepository.findByBranchIdAndProductId(branchId, productId).orElse(null);
        return modelMapper.map(inventory, InventoryDTO.class);
    }

    public List<InventoryDTO> getInventoryByBranch(Long branchId) {
        return modelMapper.map(inventoryRepository.findByBranchId(branchId), new TypeToken<List<InventoryDTO>>() {}.getType());
    }

    public List<InventoryDTO> getInventoryByProduct(Long productId) {
        return modelMapper.map(inventoryRepository.findByProductId(productId), new TypeToken<List<InventoryDTO>>() {}.getType());
    }

    public InventoryDTO saveInventory(InventoryDTO inventoryDTO) {
        Inventory inventory = modelMapper.map(inventoryDTO, Inventory.class);
        inventory.setId(null);
        if (inventory.getQuantity() == null) inventory.setQuantity(0);
        if (inventory.getQuantity() < 0) throw new IllegalStateException("Quantity cannot be negative");
        return modelMapper.map(inventoryRepository.save(inventory), InventoryDTO.class);
    }

    public InventoryDTO updateInventory(InventoryDTO inventoryDTO) {
        Inventory inventory = modelMapper.map(inventoryDTO, Inventory.class);
        if (inventory.getQuantity() == null) inventory.setQuantity(0);
        if (inventory.getQuantity() < 0) throw new IllegalStateException("Quantity cannot be negative");
        return modelMapper.map(inventoryRepository.save(inventory), InventoryDTO.class);
    }

    public String deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
        return "Inventory Deleted Successfully";
    }

}
