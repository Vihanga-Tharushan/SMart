package com.supermarket.orderservice.service;

import com.supermarket.orderservice.dto.OrderItemDTO;
import com.supermarket.orderservice.model.Order;
import com.supermarket.orderservice.model.OrderItem;
import com.supermarket.orderservice.repository.OrderItemRepository;
import com.supermarket.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findAll().stream().map(this::toDTO).toList();
    }

    public OrderItemDTO getOrderItemById(Long id) {
        return toDTO(orderItemRepository.findById(id).orElse(null));
    }

    public List<OrderItemDTO> getOrderItemsByOrder(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream().map(this::toDTO).toList();
    }

    public List<OrderItemDTO> getOrderItemsByProduct(Long productId) {
        return orderItemRepository.findByProductId(productId).stream().map(this::toDTO).toList();
    }

    public OrderItemDTO saveOrderItem(OrderItemDTO orderItemDTO) {
        Order order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(orderItemDTO.getProductId());
        item.setProductName(orderItemDTO.getProductName());
        item.setQuantity(orderItemDTO.getQuantity());
        item.setUnitPrice(orderItemDTO.getUnitPrice());
        if (item.getQuantity() == null || item.getQuantity() <= 0) throw new IllegalStateException("Quantity must be positive");
        if (item.getUnitPrice() == null || item.getUnitPrice().signum() < 0) throw new IllegalStateException("Unit price invalid");
        return toDTO(orderItemRepository.save(item));
    }

    public OrderItemDTO updateOrderItem(OrderItemDTO orderItemDTO) {
        OrderItem item = orderItemRepository.findById(orderItemDTO.getId()).orElseThrow(() -> new IllegalStateException("Item not found"));
        if (orderItemDTO.getProductName() != null) item.setProductName(orderItemDTO.getProductName());
        if (orderItemDTO.getQuantity() != null) {
            if (orderItemDTO.getQuantity() <= 0) throw new IllegalStateException("Quantity must be positive");
            item.setQuantity(orderItemDTO.getQuantity());
        }
        if (orderItemDTO.getUnitPrice() != null) {
            if (orderItemDTO.getUnitPrice().signum() < 0) throw new IllegalStateException("Unit price invalid");
            item.setUnitPrice(orderItemDTO.getUnitPrice());
        }
        return toDTO(orderItemRepository.save(item));
    }

    public String deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
        return "Order Item Deleted Successfully";
    }

    private OrderItemDTO toDTO(OrderItem item) {
        if (item == null) return null;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrder() != null ? item.getOrder().getId() : null);
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        return dto;
    }

}
