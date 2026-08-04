package com.demo.payment.controller;

import com.demo.payment.dto.PaymentDtos;
import com.demo.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDtos.PaymentResponse> initiatePayment(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody PaymentDtos.ProcessPaymentRequest request) {
        request.setUserId(userId);
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDtos.PaymentResponse> getByOrder(
            @PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrder(orderId));
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentDtos.PaymentResponse>> getMyPayments(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "payment-service"));
    }
}
