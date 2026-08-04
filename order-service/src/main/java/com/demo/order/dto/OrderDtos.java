package com.demo.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    @Data
    public static class CreateOrderRequest {
        @NotEmpty(message = "Order must have at least one item")
        private List<OrderItemRequest> items;

        @NotBlank(message = "Shipping address is required")
        private String shippingAddress;
    }

    @Data
    public static class OrderItemRequest {
        @NotBlank
        private String productId;

        @NotBlank
        private String productName;

        @Min(1)
        private Integer quantity;

        private BigDecimal unitPrice;
    }

    @Data
    public static class OrderResponse {
        private String id;
        private String userId;
        private List<OrderItemResponse> items;
        private BigDecimal totalAmount;
        private String status;
        private String shippingAddress;
        private LocalDateTime createdAt;
    }

    @Data
    public static class OrderItemResponse {
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
