package com.supermarket.inventoryservice.service;

import com.supermarket.inventoryservice.dto.StockMovementDTO;
import com.supermarket.inventoryservice.model.StockLevel;
import com.supermarket.inventoryservice.model.StockMovement;
import com.supermarket.inventoryservice.repository.StockLevelRepository;
import com.supermarket.inventoryservice.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private StockLevelRepository stockLevelRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<StockMovementDTO> getAllStockMovements() {
        return modelMapper.map(stockMovementRepository.findAll(), new TypeToken<List<StockMovementDTO>>() {}.getType());
    }

    public StockMovementDTO getStockMovementById(UUID id) {
        return modelMapper.map(stockMovementRepository.findById(id).orElse(null), StockMovementDTO.class);
    }

    public List<StockMovementDTO> getStockMovementsByBranchAndProduct(UUID branchId, UUID productId) {
        return modelMapper.map(
                stockMovementRepository.findByBranchIdAndProductIdOrderByCreatedAtDesc(branchId, productId),
                new TypeToken<List<StockMovementDTO>>() {}.getType());
    }

    public List<StockMovementDTO> getStockMovementsByReference(String referenceType, UUID referenceId) {
        return modelMapper.map(
                stockMovementRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId),
                new TypeToken<List<StockMovementDTO>>() {}.getType());
    }

    public StockMovementDTO saveStockMovement(StockMovementDTO stockMovementDTO) {
        StockMovement stockMovement = modelMapper.map(stockMovementDTO, StockMovement.class);
        stockMovement.setId(null);
        return modelMapper.map(stockMovementRepository.save(stockMovement), StockMovementDTO.class);
    }

    public StockMovementDTO recordStockMovement(StockMovementDTO stockMovementDTO) {
        if (stockMovementDTO.getIdempotencyKey() != null) {
            Optional<StockMovement> existing = stockMovementRepository.findByIdempotencyKey(stockMovementDTO.getIdempotencyKey());
            if (existing.isPresent()) {
                return modelMapper.map(existing.get(), StockMovementDTO.class);
            }
        }

        StockLevel stockLevel = stockLevelRepository
                .findByBranchIdAndProductIdForUpdate(stockMovementDTO.getBranchId(), stockMovementDTO.getProductId())
                .orElseGet(() -> {
                    StockLevel newLevel = new StockLevel();
                    newLevel.setBranchId(stockMovementDTO.getBranchId());
                    newLevel.setProductId(stockMovementDTO.getProductId());
                    return newLevel;
                });

        BigDecimal onHand = stockLevel.getQuantityOnHand() == null ? BigDecimal.ZERO : stockLevel.getQuantityOnHand();
        BigDecimal newOnHand = onHand.add(stockMovementDTO.getQuantityDelta());
        if (newOnHand.signum() < 0) {
            throw new IllegalStateException("Stock movement would result in a negative quantity on hand");
        }
        stockLevel.setQuantityOnHand(newOnHand);
        stockLevelRepository.save(stockLevel);

        StockMovement stockMovement = modelMapper.map(stockMovementDTO, StockMovement.class);
        stockMovement.setId(null);
        return modelMapper.map(stockMovementRepository.save(stockMovement), StockMovementDTO.class);
    }

    public StockMovementDTO updateStockMovement(StockMovementDTO stockMovementDTO) {
        StockMovement stockMovement = modelMapper.map(stockMovementDTO, StockMovement.class);
        return modelMapper.map(stockMovementRepository.save(stockMovement), StockMovementDTO.class);
    }

    public String deleteStockMovement(UUID id) {
        stockMovementRepository.deleteById(id);
        return "Stock Movement Deleted Successfully";
    }

}
