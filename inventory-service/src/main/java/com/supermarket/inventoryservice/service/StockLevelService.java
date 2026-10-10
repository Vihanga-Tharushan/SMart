package com.supermarket.inventoryservice.service;

import com.supermarket.inventoryservice.dto.StockLevelDTO;
import com.supermarket.inventoryservice.model.StockLevel;
import com.supermarket.inventoryservice.repository.StockLevelRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StockLevelService {

    @Autowired
    private StockLevelRepository stockLevelRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<StockLevelDTO> getAllStockLevels() {
        return modelMapper.map(stockLevelRepository.findAll(), new TypeToken<List<StockLevelDTO>>() {}.getType());
    }

    public StockLevelDTO getStockLevelById(UUID id) {
        return modelMapper.map(stockLevelRepository.findById(id).orElse(null), StockLevelDTO.class);
    }

    public StockLevelDTO getStockLevel(UUID branchId, UUID productId) {
        return modelMapper.map(
                stockLevelRepository.findByBranchIdAndProductId(branchId, productId).orElse(null),
                StockLevelDTO.class);
    }

    public List<StockLevelDTO> getStockLevelsByBranch(UUID branchId) {
        return modelMapper.map(stockLevelRepository.findByBranchId(branchId), new TypeToken<List<StockLevelDTO>>() {}.getType());
    }

    public List<StockLevelDTO> getStockLevelsByProduct(UUID productId) {
        return modelMapper.map(stockLevelRepository.findByProductId(productId), new TypeToken<List<StockLevelDTO>>() {}.getType());
    }

    public StockLevelDTO saveStockLevel(StockLevelDTO stockLevelDTO) {
        StockLevel stockLevel = modelMapper.map(stockLevelDTO, StockLevel.class);
        stockLevel.setId(null);
        return modelMapper.map(stockLevelRepository.save(stockLevel), StockLevelDTO.class);
    }

    public StockLevelDTO updateStockLevel(StockLevelDTO stockLevelDTO) {
        StockLevel stockLevel = modelMapper.map(stockLevelDTO, StockLevel.class);
        return modelMapper.map(stockLevelRepository.save(stockLevel), StockLevelDTO.class);
    }

    public String deleteStockLevel(UUID id) {
        stockLevelRepository.deleteById(id);
        return "Stock Level Deleted Successfully";
    }

}
