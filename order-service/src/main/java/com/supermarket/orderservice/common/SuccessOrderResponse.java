package com.supermarket.orderservice.common;

import com.supermarket.orderservice.dto.OrderDTO;

public class SuccessOrderRespose implements OrderResponse{

    private final OrderDTO order;

    public SuccessOrderRespose(OrderDTO order){
        this.order = order;
    }
}
