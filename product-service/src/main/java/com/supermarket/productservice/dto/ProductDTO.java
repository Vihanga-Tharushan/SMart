package com.supermarket.productservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProductDTO {

    private UUID id;
    private String sku;
    private String barcode;
    private String name;
    private String description;
    private UUID categoryId;
    private String brand;
    private String unitOfMeasure;
    private String imageUrl;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

}
