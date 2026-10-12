package com.supermarket.inventoryservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Data
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inventory_branch_product",
                columnNames = {"branch_id", "product_id"}),
        indexes = {
                @Index(name = "idx_inventory_product", columnList = "product_id"),
                @Index(name = "idx_inventory_branch", columnList = "branch_id")
        })
@Check(constraints = "quantity >= 0")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity = 0;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

}
