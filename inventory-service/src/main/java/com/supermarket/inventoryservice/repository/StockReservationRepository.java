package com.supermarket.inventoryservice.repository;

import com.supermarket.inventoryservice.model.ReservationStatus;
import com.supermarket.inventoryservice.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

    List<StockReservation> findByOrderId(UUID orderId);

    List<StockReservation> findByOrderItemId(UUID orderItemId);

    List<StockReservation> findByBranchIdAndProductIdAndStatus(UUID branchId, UUID productId, ReservationStatus status);

    List<StockReservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant expiresAt);

    Optional<StockReservation> findByOrderItemIdAndStatus(UUID orderItemId, ReservationStatus status);

}
