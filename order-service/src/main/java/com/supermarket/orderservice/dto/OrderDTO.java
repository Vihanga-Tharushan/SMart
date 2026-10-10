package com.supermarket.orderservice.dto;

import com.supermarket.orderservice.model.OrderStatus;
import com.supermarket.orderservice.model.OrderType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderDTO {

    private UUID id;
    private String orderNumber;
    private UUID customerId;
    private UUID branchId;
    private OrderType orderType;
    private OrderStatus status;
    private String currency;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal deliveryFee;
    private BigDecimal totalAmount;
    private Map<String, Object> shippingAddress;
    private String customerNotes;
    private Instant placedAt;
    private Instant cancelledAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemDTO> items = new ArrayList<>();
    private List<OrderStatusHistoryDTO> statusHistory = new ArrayList<>();

}
