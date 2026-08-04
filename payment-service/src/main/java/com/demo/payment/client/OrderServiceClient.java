package com.demo.payment.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Payment-service calls Order-service via WebClient + Eureka load balancing.
 * Used to update order status after payment succeeds or fails.
 *
 * URL "http://order-service" is resolved through Eureka — no hardcoded port.
 */
@Component
@Slf4j
public class OrderServiceClient {

    private final WebClient webClient;

    @Autowired
    public OrderServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://order-service").build();
    }

    /**
     * Called after payment is processed to update the order status.
     *
     * PAYMENT_SUCCESS → order status becomes CONFIRMED
     * PAYMENT_FAILED  → order status becomes CANCELLED
     */
    public void updateOrderStatus(String orderId, String status) {
        try {
            webClient.put()
                    .uri("/api/orders/{orderId}/status", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("status", status))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            res -> Mono.error(new RuntimeException("Order not found: " + orderId)))
                    .onStatus(HttpStatusCode::is5xxServerError,
                            res -> Mono.error(new RuntimeException("Order service error")))
                    .bodyToMono(Void.class)
                    .block();

            log.info("Order {} status updated to {} via WebClient", orderId, status);
        } catch (Exception e) {
            log.error("Failed to update order {} status: {}", orderId, e.getMessage());
        }
    }

    /** Fetch order details — used to enrich payment records */
    public OrderDto getOrder(String orderId) {
        try {
            return webClient.get()
                    .uri("/api/orders/internal/{orderId}", orderId)
                    .retrieve()
                    .bodyToMono(OrderDto.class)
                    .block();
        } catch (Exception e) {
            log.warn("Could not fetch order {}: {}", orderId, e.getMessage());
            return null;
        }
    }

    // ── Inner DTO ─────────────────────────────────────────────────────────────
    public record OrderDto(String id, String userId, String status, java.math.BigDecimal totalAmount) {}
}
