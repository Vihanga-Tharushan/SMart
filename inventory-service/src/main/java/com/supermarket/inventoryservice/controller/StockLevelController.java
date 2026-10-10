package com.supermarket.inventoryservice.controller;

import com.supermarket.inventoryservice.dto.StockLevelDTO;
import com.supermarket.inventoryservice.service.StockLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StockLevelController {

    @Autowired
    private StockLevelService stockLevelService;

    @GetMapping("/getstocklevels")
    public List<StockLevelDTO> getStockLevels() {
        return stockLevelService.getAllStockLevels();
    }

    @GetMapping("/stocklevel/{stockLevelId}")
    public StockLevelDTO getStockLevelById(@PathVariable UUID stockLevelId) {
        return stockLevelService.getStockLevelById(stockLevelId);
    }

    @GetMapping("/stocklevel/branch/{branchId}/product/{productId}")
    public StockLevelDTO getStockLevel(@PathVariable UUID branchId, @PathVariable UUID productId) {
        return stockLevelService.getStockLevel(branchId, productId);
    }

    @GetMapping("/stocklevels/branch/{branchId}")
    public List<StockLevelDTO> getStockLevelsByBranch(@PathVariable UUID branchId) {
        return stockLevelService.getStockLevelsByBranch(branchId);
    }

    @GetMapping("/stocklevels/product/{productId}")
    public List<StockLevelDTO> getStockLevelsByProduct(@PathVariable UUID productId) {
        return stockLevelService.getStockLevelsByProduct(productId);
    }

    @PostMapping("/addstocklevel")
    public StockLevelDTO saveStockLevel(@RequestBody StockLevelDTO stockLevelDTO) {
        return stockLevelService.saveStockLevel(stockLevelDTO);
    }

    @PutMapping("/updatestocklevel")
    public StockLevelDTO updateStockLevel(@RequestBody StockLevelDTO stockLevelDTO) {
        return stockLevelService.updateStockLevel(stockLevelDTO);
    }

    @DeleteMapping("/deletestocklevel/{stockLevelId}")
    public String deleteStockLevel(@PathVariable UUID stockLevelId) {
        return stockLevelService.deleteStockLevel(stockLevelId);
    }

}
