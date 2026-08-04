package com.demo.payment.consumer;

import com.demo.payment.dto.PaymentDtos;
import com.demo.payment.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @SqsListener("${aws.sqs.payment-queue-url}")
    public void handleOrderEvent(String rawMessage) {
        try {
            // SNS wraps the message in a JSON envelope
            JsonNode envelope = objectMapper.readTree(rawMessage);
            String messageBody = envelope.has("Message")
                    ? envelope.get("Message").asText()
                    : rawMessage;

            JsonNode event = objectMapper.readTree(messageBody);
            String eventType = event.get("eventType").asText();

            log.info("Received order event: {}", eventType);

            if ("ORDER_PLACED".equals(eventType)) {
                String orderId = event.get("orderId").asText();
                String userId  = event.get("userId").asText();
                BigDecimal amount = new BigDecimal(event.get("totalAmount").asText());

                PaymentDtos.ProcessPaymentRequest request = new PaymentDtos.ProcessPaymentRequest();
                request.setOrderId(orderId);
                request.setUserId(userId);
                request.setAmount(amount);
                request.setPaymentMethod("AUTO");   // auto-trigger; real method set by user

                paymentService.initiatePayment(request);
            }

        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }
}
