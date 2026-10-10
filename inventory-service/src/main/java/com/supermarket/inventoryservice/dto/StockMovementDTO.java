package com.supermarket.inventoryservice.dto;

import com.supermarket.inventoryservice.model.MovementType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class StockMovementDTO {

    private UUID id;
    private UUID branchId;
    private UUID productId;
    private MovementType movementType;
    private BigDecimal quantityDelta;
    private String referenceType;
    private UUID referenceId;
    private String reason;
    private UUID performedBy;
    private String idempotencyKey;
    private Instant createdAt;

}
