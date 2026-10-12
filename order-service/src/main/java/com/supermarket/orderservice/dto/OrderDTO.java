package com.supermarket.orderservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private Long customerId;
    private Long branchId;
    private String status;
    private String address;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private Instant cancelledAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemDTO> items = new ArrayList<>();

}
