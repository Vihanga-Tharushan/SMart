package com.supermarket.orderservice.controller;

import com.supermarket.orderservice.dto.OrderDTO;
import com.supermarket.orderservice.model.ActorType;
import com.supermarket.orderservice.model.OrderStatus;
import com.supermarket.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/getorders")
    public List<OrderDTO> getOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/order/{orderId}")
    public OrderDTO getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/order/number/{orderNumber}")
    public OrderDTO getOrderByNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber);
    }

    @GetMapping("/orders/customer/{customerId}")
    public List<OrderDTO> getOrdersByCustomer(@PathVariable UUID customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    @GetMapping("/orders/branch/{branchId}")
    public List<OrderDTO> getOrdersByBranch(@PathVariable UUID branchId) {
        return orderService.getOrdersByBranch(branchId);
    }

    @GetMapping("/orders/status/{status}")
    public List<OrderDTO> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    @PostMapping("/addorder")
    public OrderDTO saveOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.saveOrder(orderDTO);
    }

    @PutMapping("/updateorder")
    public OrderDTO updateOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.updateOrder(orderDTO);
    }

    @PutMapping("/order/{orderId}/status")
    public OrderDTO updateStatus(@PathVariable UUID orderId,
                                 @RequestParam OrderStatus status,
                                 @RequestParam(required = false) UUID changedBy,
                                 @RequestParam(required = false) ActorType actorType,
                                 @RequestParam(required = false) String reason) {
        return orderService.updateStatus(orderId, status, changedBy, actorType, reason);
    }

    @DeleteMapping("/deleteorder/{orderId}")
    public String deleteOrder(@PathVariable UUID orderId) {
        return orderService.deleteOrder(orderId);
    }

}
