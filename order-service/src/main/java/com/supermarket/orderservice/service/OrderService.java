package com.supermarket.orderservice.service;

import com.supermarket.orderservice.dto.OrderDTO;
import com.supermarket.orderservice.dto.OrderItemDTO;
import com.supermarket.orderservice.model.Order;
import com.supermarket.orderservice.model.OrderItem;
import com.supermarket.orderservice.repository.OrderItemRepository;
import com.supermarket.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toDTO).toList();
    }

    public OrderDTO getOrderById(Long id) {
        return toDTO(orderRepository.findById(id).orElse(null));
    }

    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream().map(this::toDTO).toList();
    }

    public List<OrderDTO> getOrdersByBranch(Long branchId) {
        return orderRepository.findByBranchId(branchId).stream().map(this::toDTO).toList();
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status).stream().map(this::toDTO).toList();
    }

    public OrderDTO saveOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setCustomerId(orderDTO.getCustomerId());
        order.setBranchId(orderDTO.getBranchId());
        order.setStatus(orderDTO.getStatus() != null ? orderDTO.getStatus() : "PENDING");
        order.setAddress(orderDTO.getAddress());
        order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
        buildItems(order, orderDTO.getItems());
        recalc(order);
        Order saved = orderRepository.save(order);
        return toDTO(saved);
    }

    public OrderDTO updateOrder(OrderDTO orderDTO) {
        if (orderDTO.getId() == null) throw new IllegalStateException("Order id required");
        Order order = orderRepository.findById(orderDTO.getId()).orElse(null);
        if (order == null) throw new IllegalStateException("Order not found");
        if (orderDTO.getStatus() != null) {
            order.setStatus(orderDTO.getStatus());
            if ("CANCELLED".equalsIgnoreCase(orderDTO.getStatus())) {
                order.setCancelledAt(Instant.now());
            }
        }
        if (orderDTO.getAddress() != null) order.setAddress(orderDTO.getAddress());
        if (orderDTO.getDiscountAmount() != null) order.setDiscountAmount(orderDTO.getDiscountAmount());
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            buildItems(order, orderDTO.getItems());
        }
        recalc(order);
        return toDTO(orderRepository.save(order));
    }

    public OrderDTO updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setStatus(status);
        if ("CANCELLED".equalsIgnoreCase(status)) order.setCancelledAt(Instant.now());
        return toDTO(orderRepository.save(order));
    }

    public String deleteOrder(Long id) {
        orderRepository.deleteById(id);
        return "Order Deleted Successfully";
    }

    private void buildItems(Order order, List<OrderItemDTO> items) {
        order.getItems().clear();
        if (items == null) return;
        for (OrderItemDTO dto : items) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(dto.getProductId());
            item.setProductName(dto.getProductName());
            item.setQuantity(dto.getQuantity());
            item.setUnitPrice(dto.getUnitPrice());
            order.getItems().add(item);
        }
    }

    private void recalc(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        if (discount.signum() < 0) discount = BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount);
        if (total.signum() < 0) total = BigDecimal.ZERO;
        order.setTotalAmount(total);
    }

    private OrderDTO toDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomerId());
        dto.setBranchId(order.getBranchId());
        dto.setStatus(order.getStatus());
        dto.setAddress(order.getAddress());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setCancelledAt(order.getCancelledAt());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setItems(order.getItems().stream().map(this::toItemDTO).toList());
        return dto;
    }

    private OrderItemDTO toItemDTO(OrderItem item) {
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
