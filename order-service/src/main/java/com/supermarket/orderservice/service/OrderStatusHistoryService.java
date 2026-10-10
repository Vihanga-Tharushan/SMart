package com.supermarket.orderservice.service;

import com.supermarket.orderservice.dto.OrderStatusHistoryDTO;
import com.supermarket.orderservice.model.ActorType;
import com.supermarket.orderservice.model.Order;
import com.supermarket.orderservice.model.OrderStatusHistory;
import com.supermarket.orderservice.repository.OrderRepository;
import com.supermarket.orderservice.repository.OrderStatusHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderStatusHistoryService {

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<OrderStatusHistoryDTO> getAllHistory() {
        return orderStatusHistoryRepository.findAll().stream().map(this::toDTO).toList();
    }

    public OrderStatusHistoryDTO getHistoryById(UUID id) {
        return toDTO(orderStatusHistoryRepository.findById(id).orElse(null));
    }

    public List<OrderStatusHistoryDTO> getHistoryByOrder(UUID orderId) {
        return orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId).stream().map(this::toDTO).toList();
    }

    public OrderStatusHistoryDTO saveHistory(OrderStatusHistoryDTO historyDTO) {
        Order order = orderRepository.findById(historyDTO.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setFromStatus(historyDTO.getFromStatus());
        history.setToStatus(historyDTO.getToStatus());
        history.setChangedBy(historyDTO.getChangedBy());
        history.setActorType(historyDTO.getActorType() != null ? historyDTO.getActorType() : ActorType.SYSTEM);
        history.setReason(historyDTO.getReason());
        return toDTO(orderStatusHistoryRepository.save(history));
    }

    public OrderStatusHistoryDTO updateHistory(OrderStatusHistoryDTO historyDTO) {
        OrderStatusHistory history = orderStatusHistoryRepository.findById(historyDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Status history not found"));
        if (historyDTO.getOrderId() != null) {
            Order order = orderRepository.findById(historyDTO.getOrderId())
                    .orElseThrow(() -> new IllegalStateException("Order not found"));
            history.setOrder(order);
        }
        history.setFromStatus(historyDTO.getFromStatus());
        history.setToStatus(historyDTO.getToStatus());
        history.setChangedBy(historyDTO.getChangedBy());
        if (historyDTO.getActorType() != null) {
            history.setActorType(historyDTO.getActorType());
        }
        history.setReason(historyDTO.getReason());
        return toDTO(orderStatusHistoryRepository.save(history));
    }

    public String deleteHistory(UUID id) {
        orderStatusHistoryRepository.deleteById(id);
        return "Status History Deleted Successfully";
    }

    private OrderStatusHistoryDTO toDTO(OrderStatusHistory history) {
        if (history == null) {
            return null;
        }
        OrderStatusHistoryDTO dto = new OrderStatusHistoryDTO();
        dto.setId(history.getId());
        dto.setOrderId(history.getOrder() != null ? history.getOrder().getId() : null);
        dto.setFromStatus(history.getFromStatus());
        dto.setToStatus(history.getToStatus());
        dto.setChangedBy(history.getChangedBy());
        dto.setActorType(history.getActorType());
        dto.setReason(history.getReason());
        dto.setCreatedAt(history.getCreatedAt());
        return dto;
    }

}
