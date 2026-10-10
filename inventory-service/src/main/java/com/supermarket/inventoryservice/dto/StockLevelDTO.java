package com.supermarket.inventoryservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class StockLevelDTO {

    private UUID id;
    private UUID branchId;
    private UUID productId;
    private BigDecimal quantityOnHand;
    private BigDecimal quantityReserved;
    private BigDecimal reorderLevel;
    private BigDecimal availableQuantity;
    private Instant updatedAt;

}
