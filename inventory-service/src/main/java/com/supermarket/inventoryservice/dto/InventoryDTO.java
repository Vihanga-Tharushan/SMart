package com.supermarket.inventoryservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class InventoryDTO {

    private Long id;
    private Long branchId;
    private Long productId;
    private Integer quantity;
    private Instant updatedAt;

}
