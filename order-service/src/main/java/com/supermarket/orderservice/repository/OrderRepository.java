package com.supermarket.orderservice.repository;

import com.supermarket.orderservice.model.Order;
import com.supermarket.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerId(UUID customerId);

    List<Order> findByBranchId(UUID branchId);

    List<Order> findByStatus(OrderStatus status);

    boolean existsByOrderNumber(String orderNumber);

}
