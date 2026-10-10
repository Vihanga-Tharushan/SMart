package com.supermarket.inventoryservice.repository;

import com.supermarket.inventoryservice.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    List<StockMovement> findByBranchIdAndProductIdOrderByCreatedAtDesc(UUID branchId, UUID productId);

    List<StockMovement> findByReferenceTypeAndReferenceId(String referenceType, UUID referenceId);

    Optional<StockMovement> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

}
