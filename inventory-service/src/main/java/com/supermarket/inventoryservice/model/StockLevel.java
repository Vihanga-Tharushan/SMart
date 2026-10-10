package com.supermarket.inventoryservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@Table(name = "stock_levels",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_stock_levels_branch_product",
                columnNames = {"branch_id", "product_id"}),
        indexes = {
                @Index(name = "idx_stock_levels_branch", columnList = "branch_id"),
                @Index(name = "idx_stock_levels_product", columnList = "product_id")
        })
@Check(constraints = "quantity_on_hand >= 0 AND quantity_reserved >= 0 AND reorder_level >= 0")
public class StockLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity_on_hand", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantityOnHand = BigDecimal.ZERO;

    @Column(name = "quantity_reserved", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantityReserved = BigDecimal.ZERO;

    @Column(name = "reorder_level", nullable = false, precision = 12, scale = 3)
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public BigDecimal getAvailableQuantity() {
        BigDecimal onHand = quantityOnHand == null ? BigDecimal.ZERO : quantityOnHand;
        BigDecimal reserved = quantityReserved == null ? BigDecimal.ZERO : quantityReserved;
        return onHand.subtract(reserved);
    }

}
