package com.supermarket.inventoryservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@Table(name = "stock_movements",
        indexes = {
                @Index(name = "idx_stock_movements_branch_product", columnList = "branch_id,product_id"),
                @Index(name = "idx_stock_movements_type", columnList = "movement_type"),
                @Index(name = "idx_stock_movements_reference", columnList = "reference_type,reference_id"),
                @Index(name = "idx_stock_movements_created", columnList = "created_at")
        })
@Check(constraints = "quantity_delta <> 0")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @Column(name = "quantity_delta", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantityDelta;

    @Column(name = "reference_type", length = 30)
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String reason;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "idempotency_key", length = 150, unique = true)
    private String idempotencyKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

}
