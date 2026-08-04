package com.demo.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Calls USER-SERVICE via Eureka service discovery.
 * URL "http://user-service" is resolved by @LoadBalanced WebClient
 * to whichever instance Eureka knows about.
 */
@Component
@Slf4j
public class UserServiceClient {

    private final WebClient webClient;

    @Autowired
    public UserServiceClient(WebClient.Builder builder) {
        // base URL uses the Eureka service name, not localhost:port
        this.webClient = builder.baseUrl("http://user-service").build();
    }

    /**
     * Fetch user profile from user-service.
     * Returns null (with a warning) if the service is unreachable.
     */
    public UserDto getUserById(String userId) {
        try {
            return webClient.get()
                    .uri("/api/users/internal/{id}", userId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            response -> Mono.error(new RuntimeException("User not found: " + userId)))
                    .onStatus(status -> status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("User service error")))
                    .bodyToMono(UserDto.class)
                    .block();   // block() is acceptable inside a Servlet (non-reactive) service
        } catch (Exception e) {
            log.warn("Could not fetch user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    // ── Inner DTO (matches UserService response shape) ──────────────────────
    public record UserDto(String id, String name, String email, String phone, String role) {}
}
