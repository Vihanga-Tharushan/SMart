package com.supermarket.orderservice.controller;

import com.supermarket.orderservice.dto.OrderItemDTO;
import com.supermarket.orderservice.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/getorderitems")
    public List<OrderItemDTO> getOrderItems() {
        return orderItemService.getAllOrderItems();
    }

    @GetMapping("/orderitem/{orderItemId}")
    public OrderItemDTO getOrderItemById(@PathVariable UUID orderItemId) {
        return orderItemService.getOrderItemById(orderItemId);
    }

    @GetMapping("/orderitems/order/{orderId}")
    public List<OrderItemDTO> getOrderItemsByOrder(@PathVariable UUID orderId) {
        return orderItemService.getOrderItemsByOrder(orderId);
    }

    @GetMapping("/orderitems/product/{productId}")
    public List<OrderItemDTO> getOrderItemsByProduct(@PathVariable UUID productId) {
        return orderItemService.getOrderItemsByProduct(productId);
    }

    @PostMapping("/addorderitem")
    public OrderItemDTO saveOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        return orderItemService.saveOrderItem(orderItemDTO);
    }

    @PutMapping("/updateorderitem")
    public OrderItemDTO updateOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        return orderItemService.updateOrderItem(orderItemDTO);
    }

    @DeleteMapping("/deleteorderitem/{orderItemId}")
    public String deleteOrderItem(@PathVariable UUID orderItemId) {
        return orderItemService.deleteOrderItem(orderItemId);
    }

}
