package com.demo.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDtos {

    @Data
    public static class CreateProductRequest {
        @NotBlank
        private String name;
        private String description;
        private String category;
        @NotNull @Min(0)
        private BigDecimal price;
        @Min(0)
        private Integer stock;
       // private String imageUrl;
    }

    @Data
    public static class UpdateStockRequest {
        private Integer quantity;  // negative to decrement
    }

    @Data
    public static class ProductResponse {
        private String id;
        private String name;
        private String description;
        private String category;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
        private Boolean active;
        private LocalDateTime createdAt;
    }
}
