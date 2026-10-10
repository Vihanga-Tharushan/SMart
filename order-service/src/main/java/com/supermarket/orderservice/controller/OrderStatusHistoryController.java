package com.supermarket.orderservice.controller;

import com.supermarket.orderservice.dto.OrderStatusHistoryDTO;
import com.supermarket.orderservice.service.OrderStatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderStatusHistoryController {

    @Autowired
    private OrderStatusHistoryService orderStatusHistoryService;

    @GetMapping("/getorderhistory")
    public List<OrderStatusHistoryDTO> getHistory() {
        return orderStatusHistoryService.getAllHistory();
    }

    @GetMapping("/orderhistory/{historyId}")
    public OrderStatusHistoryDTO getHistoryById(@PathVariable UUID historyId) {
        return orderStatusHistoryService.getHistoryById(historyId);
    }

    @GetMapping("/orderhistory/order/{orderId}")
    public List<OrderStatusHistoryDTO> getHistoryByOrder(@PathVariable UUID orderId) {
        return orderStatusHistoryService.getHistoryByOrder(orderId);
    }

    @PostMapping("/addorderhistory")
    public OrderStatusHistoryDTO saveHistory(@RequestBody OrderStatusHistoryDTO historyDTO) {
        return orderStatusHistoryService.saveHistory(historyDTO);
    }

    @PutMapping("/updateorderhistory")
    public OrderStatusHistoryDTO updateHistory(@RequestBody OrderStatusHistoryDTO historyDTO) {
        return orderStatusHistoryService.updateHistory(historyDTO);
    }

    @DeleteMapping("/deleteorderhistory/{historyId}")
    public String deleteHistory(@PathVariable UUID historyId) {
        return orderStatusHistoryService.deleteHistory(historyId);
    }

}
