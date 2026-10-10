package com.supermarket.orderservice.service;

import com.supermarket.orderservice.dto.OrderDTO;
import com.supermarket.orderservice.dto.OrderItemDTO;
import com.supermarket.orderservice.dto.OrderStatusHistoryDTO;
import com.supermarket.orderservice.model.ActorType;
import com.supermarket.orderservice.model.Order;
import com.supermarket.orderservice.model.OrderItem;
import com.supermarket.orderservice.model.OrderStatus;
import com.supermarket.orderservice.model.OrderStatusHistory;
import com.supermarket.orderservice.model.OrderType;
import com.supermarket.orderservice.repository.OrderItemRepository;
import com.supermarket.orderservice.repository.OrderRepository;
import com.supermarket.orderservice.repository.OrderStatusHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toDTO).toList();
    }

    public OrderDTO getOrderById(UUID id) {
        return toDTO(orderRepository.findById(id).orElse(null));
    }

    public OrderDTO getOrderByNumber(String orderNumber) {
        return toDTO(orderRepository.findByOrderNumber(orderNumber).orElse(null));
    }

    public List<OrderDTO> getOrdersByCustomer(UUID customerId) {
        return orderRepository.findByCustomerId(customerId).stream().map(this::toDTO).toList();
    }

    public List<OrderDTO> getOrdersByBranch(UUID branchId) {
        return orderRepository.findByBranchId(branchId).stream().map(this::toDTO).toList();
    }

    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream().map(this::toDTO).toList();
    }

    public OrderDTO saveOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderNumber(orderDTO.getOrderNumber() != null ? orderDTO.getOrderNumber() : generateOrderNumber());
        order.setCustomerId(orderDTO.getCustomerId());
        order.setBranchId(orderDTO.getBranchId());
        order.setOrderType(orderDTO.getOrderType() != null ? orderDTO.getOrderType() : OrderType.ONLINE);
        order.setStatus(OrderStatus.PENDING);
        order.setCurrency(orderDTO.getCurrency() != null ? orderDTO.getCurrency() : "LKR");
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setCustomerNotes(orderDTO.getCustomerNotes());
        order.setPlacedAt(orderDTO.getPlacedAt() != null ? orderDTO.getPlacedAt() : Instant.now());

        buildItems(order, orderDTO.getItems());
        recalculateTotals(order, orderDTO.getDiscountAmount(), orderDTO.getTaxAmount(), orderDTO.getDeliveryFee());

        Order savedOrder = orderRepository.save(order);
        addHistory(savedOrder, null, savedOrder.getStatus(), savedOrder.getCustomerId(), ActorType.CUSTOMER, "Order placed");
        return toDTO(savedOrder);
    }

    public OrderDTO updateOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (orderDTO.getOrderNumber() != null) order.setOrderNumber(orderDTO.getOrderNumber());
        order.setCustomerId(orderDTO.getCustomerId());
        order.setBranchId(orderDTO.getBranchId());
        if (orderDTO.getOrderType() != null) order.setOrderType(orderDTO.getOrderType());
        if (orderDTO.getCurrency() != null) order.setCurrency(orderDTO.getCurrency());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setCustomerNotes(orderDTO.getCustomerNotes());
        if (orderDTO.getPlacedAt() != null) order.setPlacedAt(orderDTO.getPlacedAt());

        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            buildItems(order, orderDTO.getItems());
        }
        recalculateTotals(order, orderDTO.getDiscountAmount(), orderDTO.getTaxAmount(), orderDTO.getDeliveryFee());

        return toDTO(orderRepository.save(order));
    }

    public OrderDTO updateStatus(UUID id, OrderStatus newStatus, UUID changedBy, ActorType actorType, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        OrderStatus fromStatus = order.getStatus();
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.CANCELLED) {
            order.setCancelledAt(Instant.now());
        }
        Order savedOrder = orderRepository.save(order);
        addHistory(savedOrder, fromStatus, newStatus, changedBy,
                actorType != null ? actorType : ActorType.SYSTEM, reason);
        return toDTO(savedOrder);
    }

    public String deleteOrder(UUID id) {
        orderRepository.deleteById(id);
        return "Order Deleted Successfully";
    }

    private OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerId(order.getCustomerId());
        dto.setBranchId(order.getBranchId());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setCurrency(order.getCurrency());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCustomerNotes(order.getCustomerNotes());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setCancelledAt(order.getCancelledAt());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setItems(order.getItems().stream().map(this::toItemDTO).toList());
        dto.setStatusHistory(orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(order.getId())
                .stream().map(this::toHistoryDTO).toList());
        return dto;
    }

    private OrderItemDTO toItemDTO(OrderItem item) {
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

    private OrderStatusHistoryDTO toHistoryDTO(OrderStatusHistory history) {
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

    private void buildItems(Order order, List<OrderItemDTO> itemDTOs) {
        order.getItems().clear();
        if (itemDTOs == null) {
            return;
        }
        for (OrderItemDTO itemDTO : itemDTOs) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setProductSku(itemDTO.getProductSku());
            item.setUnitOfMeasure(itemDTO.getUnitOfMeasure());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            BigDecimal discount = itemDTO.getDiscountAmount() != null ? itemDTO.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal tax = itemDTO.getTaxAmount() != null ? itemDTO.getTaxAmount() : BigDecimal.ZERO;
            item.setDiscountAmount(discount);
            item.setTaxAmount(tax);
            BigDecimal gross = itemDTO.getUnitPrice().multiply(itemDTO.getQuantity());
            item.setLineTotal(gross.subtract(discount).add(tax));
            order.getItems().add(item);
        }
    }

    private void recalculateTotals(Order order, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal deliveryFee) {
        BigDecimal subtotal = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        BigDecimal delivery = deliveryFee != null ? deliveryFee : BigDecimal.ZERO;
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setTaxAmount(tax);
        order.setDeliveryFee(delivery);
        order.setTotalAmount(subtotal.subtract(discount).add(tax).add(delivery));
    }

    private void addHistory(Order order, OrderStatus fromStatus, OrderStatus toStatus, UUID changedBy,
                            ActorType actorType, String reason) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setChangedBy(changedBy);
        history.setActorType(actorType);
        history.setReason(reason);
        orderStatusHistoryRepository.save(history);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
