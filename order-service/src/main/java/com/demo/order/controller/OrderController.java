package com.demo.order.controller;

import com.demo.order.client.RestTempleteCall;
import com.demo.order.dto.OrderDtos;
import com.demo.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RestTempleteCall restTempleteCall;

    @PostMapping
    public ResponseEntity<OrderDtos.OrderResponse> createOrder(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody OrderDtos.CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId, request));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDtos.OrderResponse> getOrder(
            @PathVariable String orderId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDtos.OrderResponse>> getMyOrders(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDtos.OrderResponse> updateStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, body.get("status")));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "order-service"));
    }

    @GetMapping("/callingproductservice")
    public String restTemplateCall()
    {
        return restTempleteCall.restTemplateCall();
    }

}
