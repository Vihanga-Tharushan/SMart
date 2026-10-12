package com.supermarket.productservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class ProductDTO {

    private Long id;
    private String brand;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private String description;
    private String unit;
    private String imageUrl;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

}
