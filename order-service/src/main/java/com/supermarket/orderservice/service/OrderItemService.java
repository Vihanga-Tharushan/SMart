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
import java.util.UUID;

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

    public OrderItemDTO getOrderItemById(UUID id) {
        return toDTO(orderItemRepository.findById(id).orElse(null));
    }

    public List<OrderItemDTO> getOrderItemsByOrder(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId).stream().map(this::toDTO).toList();
    }

    public List<OrderItemDTO> getOrderItemsByProduct(UUID productId) {
        return orderItemRepository.findByProductId(productId).stream().map(this::toDTO).toList();
    }

    public OrderItemDTO saveOrderItem(OrderItemDTO orderItemDTO) {
        Order order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(orderItemDTO.getProductId());
        item.setProductName(orderItemDTO.getProductName());
        item.setProductSku(orderItemDTO.getProductSku());
        item.setUnitOfMeasure(orderItemDTO.getUnitOfMeasure());
        item.setQuantity(orderItemDTO.getQuantity());
        item.setUnitPrice(orderItemDTO.getUnitPrice());
        BigDecimal discount = orderItemDTO.getDiscountAmount() != null ? orderItemDTO.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal tax = orderItemDTO.getTaxAmount() != null ? orderItemDTO.getTaxAmount() : BigDecimal.ZERO;
        item.setDiscountAmount(discount);
        item.setTaxAmount(tax);
        BigDecimal gross = orderItemDTO.getUnitPrice().multiply(orderItemDTO.getQuantity());
        item.setLineTotal(orderItemDTO.getLineTotal() != null
                ? orderItemDTO.getLineTotal()
                : gross.subtract(discount).add(tax));
        return toDTO(orderItemRepository.save(item));
    }

    public OrderItemDTO updateOrderItem(OrderItemDTO orderItemDTO) {
        OrderItem item = orderItemRepository.findById(orderItemDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Order item not found"));
        if (orderItemDTO.getOrderId() != null) {
            Order order = orderRepository.findById(orderItemDTO.getOrderId())
                    .orElseThrow(() -> new IllegalStateException("Order not found"));
            item.setOrder(order);
        }
        item.setProductId(orderItemDTO.getProductId());
        item.setProductName(orderItemDTO.getProductName());
        item.setProductSku(orderItemDTO.getProductSku());
        item.setUnitOfMeasure(orderItemDTO.getUnitOfMeasure());
        item.setQuantity(orderItemDTO.getQuantity());
        item.setUnitPrice(orderItemDTO.getUnitPrice());
        item.setDiscountAmount(orderItemDTO.getDiscountAmount() != null ? orderItemDTO.getDiscountAmount() : BigDecimal.ZERO);
        item.setTaxAmount(orderItemDTO.getTaxAmount() != null ? orderItemDTO.getTaxAmount() : BigDecimal.ZERO);
        if (orderItemDTO.getLineTotal() != null) {
            item.setLineTotal(orderItemDTO.getLineTotal());
        }
        return toDTO(orderItemRepository.save(item));
    }

    public String deleteOrderItem(UUID id) {
        orderItemRepository.deleteById(id);
        return "Order Item Deleted Successfully";
    }

    private OrderItemDTO toDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrder() != null ? item.getOrder().getId() : null);
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setProductSku(item.getProductSku());
        dto.setUnitOfMeasure(item.getUnitOfMeasure());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscountAmount(item.getDiscountAmount());
        dto.setTaxAmount(item.getTaxAmount());
        dto.setLineTotal(item.getLineTotal());
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }

}
