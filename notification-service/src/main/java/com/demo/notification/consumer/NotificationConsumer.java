package com.demo.notification.consumer;

import com.demo.notification.model.NotificationMessage;
import com.demo.notification.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public void handleEvent(String rawMessage) {
        try {
            // Unwrap SNS envelope if present
            JsonNode envelope = objectMapper.readTree(rawMessage);
            String messageBody = envelope.has("Message")
                    ? envelope.get("Message").asText()
                    : rawMessage;

            JsonNode event = objectMapper.readTree(messageBody);
            String eventType = event.get("eventType").asText();
            String userId    = event.has("userId") ? event.get("userId").asText() : "unknown";

            log.info("Notification consumer received event: {} for user: {}", eventType, userId);

            String body = buildNotificationBody(event, eventType);

            NotificationMessage message = NotificationMessage.builder()
                    .userId(userId)
                    .eventType(eventType)
                    .subject(eventType.replace("_", " "))
                    .body(body)
                    .channel("ALL")
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationService.send(message);

        } catch (Exception e) {
            log.error("Failed to process notification event: {}", e.getMessage(), e);
        }
    }

    private String buildNotificationBody(JsonNode event, String eventType) {
        return switch (eventType) {
            case "ORDER_PLACED" ->
                    "Order " + event.path("orderId").asText() +
                    " placed successfully. Total: ₹" + event.path("totalAmount").asText();
            case "PAYMENT_SUCCESS" ->
                    "Payment of ₹" + event.path("amount").asText() +
                    " successful. Transaction ID: " + event.path("transactionId").asText();
            case "PAYMENT_FAILED" ->
                    "Payment of ₹" + event.path("amount").asText() +
                    " failed for order " + event.path("orderId").asText() + ". Please retry.";
            case "ORDER_STATUS_UPDATED" ->
                    "Order " + event.path("orderId").asText() +
                    " status updated to: " + event.path("status").asText();
            default -> eventType + " event received.";
        };
    }
}
