package com.supermarket.orderservice.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    READY_FOR_DELIVERY,
    OUT_FOR_DELIVERY,
    COMPLETED,
    CANCELLED,
    AWAITING_PAYMENT,
    PAYMENT_FAILED,
    REJECTED
}
