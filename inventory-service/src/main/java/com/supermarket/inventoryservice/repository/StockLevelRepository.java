package com.supermarket.inventoryservice.repository;

import com.supermarket.inventoryservice.model.StockLevel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, UUID> {

    Optional<StockLevel> findByBranchIdAndProductId(UUID branchId, UUID productId);

    List<StockLevel> findByBranchId(UUID branchId);

    List<StockLevel> findByProductId(UUID productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockLevel s WHERE s.branchId = :branchId AND s.productId = :productId")
    Optional<StockLevel> findByBranchIdAndProductIdForUpdate(@Param("branchId") UUID branchId,
                                                             @Param("productId") UUID productId);

}
