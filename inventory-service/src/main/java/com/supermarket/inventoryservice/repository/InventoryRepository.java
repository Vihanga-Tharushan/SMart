package com.supermarket.inventoryservice.repository;

import com.supermarket.inventoryservice.model.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByBranchIdAndProductId(Long branchId, Long productId);

    List<Inventory> findByBranchId(Long branchId);

    List<Inventory> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.branchId = :branchId AND i.productId = :productId")
    Optional<Inventory> findByBranchIdAndProductIdForUpdate(@Param("branchId") Long branchId,
                                                            @Param("productId") Long productId);
}
