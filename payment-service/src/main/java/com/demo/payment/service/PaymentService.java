package com.demo.payment.service;

import com.demo.payment.client.OrderServiceClient;
import com.demo.payment.dto.PaymentDtos;
import com.demo.payment.model.Payment;
import com.demo.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository  paymentRepository;
    private final OrderServiceClient orderServiceClient; // ← WebClient call
    private final SnsClient          snsClient;
    private final ObjectMapper       objectMapper;

    @Value("${aws.sns.order-topic-arn}")
    private String orderTopicArn;

    @Transactional
    public PaymentDtos.PaymentResponse initiatePayment(PaymentDtos.ProcessPaymentRequest request) {
        paymentRepository.findByOrderId(request.getOrderId()).ifPresent(p -> {
            throw new RuntimeException("Payment already exists for order: " + request.getOrderId());
        });

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        return processPaymentGateway(paymentRepository.save(payment));
    }

    private PaymentDtos.PaymentResponse processPaymentGateway(Payment payment) {
        // Simulate payment gateway (replace with Razorpay/Stripe SDK)
        boolean success = Math.random() > 0.1;

        payment.setStatus(success ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setProcessedAt(LocalDateTime.now());
        Payment updated = paymentRepository.save(payment);

        // Step: Update order status via WebClient -> order-service (Eureka lb)
        String newOrderStatus = success ? "CONFIRMED" : "CANCELLED";
        orderServiceClient.updateOrderStatus(payment.getOrderId(), newOrderStatus);
        log.info("Order {} marked {} after payment {}", payment.getOrderId(),
                newOrderStatus, payment.getStatus());

        // Step: Publish payment result to SNS -> notification-service picks it up
        publishPaymentEvent(updated);

        return mapToResponse(updated);
    }

    public PaymentDtos.PaymentResponse getPaymentByOrder(String orderId) {
        return mapToResponse(paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId)));
    }

    public List<PaymentDtos.PaymentResponse> getUserPayments(String userId) {
        return paymentRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void publishPaymentEvent(Payment payment) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType",     "PAYMENT_" + payment.getStatus().name());
            event.put("paymentId",     payment.getId());
            event.put("orderId",       payment.getOrderId());
            event.put("userId",        payment.getUserId());
            event.put("amount",        payment.getAmount());
            event.put("transactionId", payment.getTransactionId());
            event.put("status",        payment.getStatus().name());
            event.put("timestamp",     LocalDateTime.now().toString());

            String eventType = "PAYMENT_" + payment.getStatus().name();
            snsClient.publish(PublishRequest.builder()
                    .topicArn(orderTopicArn)
                    .message(objectMapper.writeValueAsString(event))
                    .subject(eventType)
                    .messageAttributes(Map.of("eventType", MessageAttributeValue.builder()
                            .dataType("String").stringValue(eventType).build()))
                    .build());
            log.info("SNS published: {} for payment {}", eventType, payment.getId());
        } catch (Exception e) {
            log.error("SNS publish failed: {}", e.getMessage());
        }
    }

    private PaymentDtos.PaymentResponse mapToResponse(Payment p) {
        PaymentDtos.PaymentResponse r = new PaymentDtos.PaymentResponse();
        r.setId(p.getId()); r.setOrderId(p.getOrderId()); r.setUserId(p.getUserId());
        r.setAmount(p.getAmount()); r.setStatus(p.getStatus().name());
        r.setTransactionId(p.getTransactionId()); r.setPaymentMethod(p.getPaymentMethod());
        r.setCreatedAt(p.getCreatedAt()); r.setProcessedAt(p.getProcessedAt());
        return r;
    }
}
