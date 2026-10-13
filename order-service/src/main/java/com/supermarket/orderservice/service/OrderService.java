package com.supermarket.orderservice.service;


import com.supermarket.orderservice.dto.OrderDTO;
import com.supermarket.orderservice.dto.OrderItemDTO;
import com.supermarket.orderservice.dto.product.ProductDTO;
import com.supermarket.orderservice.model.Order;
import com.supermarket.orderservice.model.OrderItem;
import com.supermarket.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final WebClient webClient;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Value("${inventory.service.url:http://localhost:8082}")
    private String inventoryServiceUrl;

    @Value("${product.service.url:http://localhost:8080}")
    private String productServiceUrl;

    public OrderService(WebClient webClient, OrderRepository orderRepository, ModelMapper modelMapper) {
        this.webClient = webClient;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;

        this.modelMapper.createTypeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setItems));
    }

    public List<OrderDTO> getAllOrders() {

        List<Order> orders = orderRepository.findAll();
        return modelMapper.map(orders, new TypeToken<List<OrderDTO>>() {}.getType());
    }

    public OrderDTO getOrderById(Long id) {

        return modelMapper.map(orderRepository.findById(id).orElse(null), OrderDTO.class);
    }

    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return modelMapper.map(orders, new TypeToken<List<OrderDTO>>() {}.getType());
    }

    public List<OrderDTO> getOrdersByBranch(Long branchId) {
        List<Order> orders = orderRepository.findByBranchId(branchId);
        return modelMapper.map(orders, new TypeToken<List<OrderDTO>>() {}.getType());
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return modelMapper.map(orders, new TypeToken<List<OrderDTO>>() {}.getType());
    }

    public OrderDTO saveOrder(OrderDTO orderDTO) {

        Order order = modelMapper.map(orderDTO, Order.class);
        order.setId(null);
        if (order.getStatus() == null) order.setStatus("PENDING");
        if (order.getDiscountAmount() == null) order.setDiscountAmount(BigDecimal.ZERO);
        buildItems(order, orderDTO.getItems());
        recalc(order);
        Order saved = orderRepository.save(order);
        return modelMapper.map(saved, OrderDTO.class);
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
        return modelMapper.map(orderRepository.save(order), OrderDTO.class);
    }

    public OrderDTO updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setStatus(status);
        if ("CANCELLED".equalsIgnoreCase(status)) order.setCancelledAt(Instant.now());
        return modelMapper.map(orderRepository.save(order), OrderDTO.class);
    }

    public String deleteOrder(Long id) {
        orderRepository.deleteById(id);
        return "Order Deleted Successfully";
    }
    private void buildItems(Order order, List<OrderItemDTO> items) {

        order.getItems().clear();
        Long branchId = order.getBranchId();
        if (branchId == null) {
            throw new IllegalStateException("Branch id is required to validate inventory");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("No item selected");
        }
        for (OrderItemDTO dto : items) {
            if (dto == null) {
                throw new IllegalStateException("Item cannot be null");
            }
            Long productId = dto.getProductId();
            if (productId == null) {
                throw new IllegalStateException("Product id is required");
            }
            Integer quantity = dto.getQuantity();
            if (quantity == null || quantity <= 0) {
                throw new IllegalStateException("Quantity must be greater than 0 for product " + productId);
            }

            try {
                ProductDTO product = webClient.get()
                        .uri(productServiceUrl + "/api/v1/product/{productId}", productId)
                        .retrieve()
                        .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                                resp -> Mono.error(new IllegalStateException(
                                        "Product not found for product " + productId)))
                        .onStatus(status -> status.is4xxClientError(),
                                resp -> Mono.error(new IllegalStateException(
                                        "Product request rejected for product " + productId)))
                        .onStatus(HttpStatusCode::is5xxServerError,
                                resp -> Mono.error(new IllegalStateException(
                                        "Product service error for product " + productId)))
                        .bodyToMono(ProductDTO.class)
                        .timeout(Duration.ofSeconds(5))
                        .block();
                if (product == null || !Boolean.TRUE.equals(product.getIsActive())) {
                    throw new IllegalStateException("Product is not active for product " + productId);
                }

                com.supermarket.orderservice.dto.inventory.InventoryDTO inventoryResponse = webClient.get()
                        .uri(inventoryServiceUrl + "/api/v1/inventory/branch/{branchId}/product/{productId}", branchId, productId)
                        .retrieve()
                        .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                                resp -> Mono.error(new IllegalStateException(
                                        "Inventory not found for product " + productId + " at branch " + branchId)))
                        .onStatus(status -> status.is4xxClientError(),
                                resp -> Mono.error(new IllegalStateException(
                                        "Inventory request rejected for product " + productId + " at branch " + branchId)))
                        .onStatus(HttpStatusCode::is5xxServerError,
                                resp -> Mono.error(new IllegalStateException(
                                        "Inventory service error for product " + productId + " at branch " + branchId)))
                        .bodyToMono(com.supermarket.orderservice.dto.inventory.InventoryDTO.class)
                        .timeout(Duration.ofSeconds(5))
                        .block();
                if (inventoryResponse == null) {
                    throw new IllegalStateException(
                            "Inventory lookup failed for product " + productId + " at branch " + branchId);
                }
                if (inventoryResponse.getQuantity() < quantity) {
                    throw new IllegalStateException("Item not available");
                }

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProductId(dto.getProductId());
                item.setProductName(dto.getProductName());
                item.setQuantity(dto.getQuantity());
                item.setUnitPrice(dto.getUnitPrice());
                order.getItems().add(item);

            } catch (WebClientResponseException e) {
                log.error("Inventory lookup failed for product {} at branch {}: status={}, body={}",
                        productId, branchId, e.getStatusCode(), e.getResponseBodyAsString(), e);
                throw new IllegalStateException("Inventory lookup failed for product " + productId);
            } catch (IllegalStateException e) {
                throw e;
            } catch (Exception e) {
                log.error("Inventory lookup error for product {} at branch {}", productId, branchId, e);
                throw new IllegalStateException("Inventory lookup failed for product " + productId);
            }
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

}
