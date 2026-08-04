package com.demo.product.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;

import jakarta.validation.constraints.NotBlank;


@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    @Indexed
    @NotBlank
    private String name;

    @TextIndexed
    private String description;

    @Indexed
    @NotBlank
    private String category;

    @NotNull
    private BigDecimal price;

    @Builder.Default
    private Integer stock = 0;

    private String imageUrl;


    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;
}