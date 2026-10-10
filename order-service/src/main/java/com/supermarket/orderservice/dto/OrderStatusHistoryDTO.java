package com.supermarket.orderservice.dto;

import com.supermarket.orderservice.model.ActorType;
import com.supermarket.orderservice.model.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderStatusHistoryDTO {

    private UUID id;
    private UUID orderId;
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private UUID changedBy;
    private ActorType actorType;
    private String reason;
    private Instant createdAt;

}
