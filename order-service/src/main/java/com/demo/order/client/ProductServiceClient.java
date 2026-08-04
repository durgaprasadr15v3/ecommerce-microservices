package com.demo.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Calls PRODUCT-SERVICE via Eureka service discovery.
 * Used by OrderService to:
 *   1. Validate product exists
 *   2. Check stock availability
 *   3. Decrement stock after order is placed
 */
@Component
@Slf4j
public class ProductServiceClient {

    private final WebClient webClient;

    @Autowired
    public ProductServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://product-service").build();
    }

    /** Fetch a single product. Returns null if not found. */
    public ProductDto getProductById(String productId) {
        try {
            return webClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            res -> Mono.error(new RuntimeException("Product not found: " + productId)))
                    .bodyToMono(ProductDto.class)
                    .block();
        } catch (Exception e) {
            log.warn("Could not fetch product {}: {}", productId, e.getMessage());
            return null;
        }
    }

    /**
     * Deduct stock after order is confirmed.
     * quantity is negative to decrement (e.g. -2 means reduce by 2).
     */
    public ProductDto decrementStock(String productId, int quantity) {
        try {
            return webClient.patch()
                    .uri("/api/products/{id}/stock", productId)
                    .bodyValue(new StockUpdateRequest(-quantity))  // send negative delta
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            res -> Mono.error(new RuntimeException("Stock update failed: " + productId)))
                    .bodyToMono(ProductDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to decrement stock for product {}: {}", productId, e.getMessage());
            return null;
        }
    }

    // ── Inner DTOs ────────────────────────────────────────────────────────────
    public record ProductDto(
            String id,
            String name,
            String category,
            BigDecimal price,
            Integer stock,
            Boolean active
    ) {}

    public record StockUpdateRequest(Integer quantity) {}
}
