package com.supermarket.orderservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderItemDTO {

    private UUID id;
    private UUID orderId;
    private UUID productId;
    private String productName;
    private String productSku;
    private String unitOfMeasure;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal lineTotal;
    private Instant createdAt;

}
