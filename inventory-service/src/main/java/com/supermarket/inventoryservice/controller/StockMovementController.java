package com.supermarket.inventoryservice.controller;

import com.supermarket.inventoryservice.dto.StockMovementDTO;
import com.supermarket.inventoryservice.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StockMovementController {

    @Autowired
    private StockMovementService stockMovementService;

    @GetMapping("/getstockmovements")
    public List<StockMovementDTO> getStockMovements() {
        return stockMovementService.getAllStockMovements();
    }

    @GetMapping("/stockmovement/{stockMovementId}")
    public StockMovementDTO getStockMovementById(@PathVariable UUID stockMovementId) {
        return stockMovementService.getStockMovementById(stockMovementId);
    }

    @GetMapping("/stockmovements/branch/{branchId}/product/{productId}")
    public List<StockMovementDTO> getStockMovementsByBranchAndProduct(@PathVariable UUID branchId, @PathVariable UUID productId) {
        return stockMovementService.getStockMovementsByBranchAndProduct(branchId, productId);
    }

    @GetMapping("/stockmovements/reference")
    public List<StockMovementDTO> getStockMovementsByReference(@RequestParam String referenceType,
                                                               @RequestParam UUID referenceId) {
        return stockMovementService.getStockMovementsByReference(referenceType, referenceId);
    }

    @PostMapping("/addstockmovement")
    public StockMovementDTO saveStockMovement(@RequestBody StockMovementDTO stockMovementDTO) {
        return stockMovementService.saveStockMovement(stockMovementDTO);
    }

    @PostMapping("/recordstockmovement")
    public StockMovementDTO recordStockMovement(@RequestBody StockMovementDTO stockMovementDTO) {
        return stockMovementService.recordStockMovement(stockMovementDTO);
    }

    @PutMapping("/updatestockmovement")
    public StockMovementDTO updateStockMovement(@RequestBody StockMovementDTO stockMovementDTO) {
        return stockMovementService.updateStockMovement(stockMovementDTO);
    }

    @DeleteMapping("/deletestockmovement/{stockMovementId}")
    public String deleteStockMovement(@PathVariable UUID stockMovementId) {
        return stockMovementService.deleteStockMovement(stockMovementId);
    }

}
