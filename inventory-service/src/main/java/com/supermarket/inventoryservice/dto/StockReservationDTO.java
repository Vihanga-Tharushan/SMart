package com.supermarket.inventoryservice.dto;

import com.supermarket.inventoryservice.model.ReservationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class StockReservationDTO {

    private UUID id;
    private UUID orderId;
    private UUID orderItemId;
    private UUID branchId;
    private UUID productId;
    private BigDecimal quantity;
    private ReservationStatus status;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

}
