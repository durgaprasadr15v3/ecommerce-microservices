package com.demo.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDtos {

    @Data
    public static class ProcessPaymentRequest {
        private String orderId;
        private String userId;
        private BigDecimal amount;
        private String paymentMethod;  // CARD, UPI, NETBANKING
    }

    @Data
    public static class PaymentResponse {
        private String id;
        private String orderId;
        private String userId;
        private BigDecimal amount;
        private String status;
        private String transactionId;
        private String paymentMethod;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;
    }
}
