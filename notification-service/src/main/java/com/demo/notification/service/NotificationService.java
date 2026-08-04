package com.demo.notification.service;

import com.demo.notification.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void send(NotificationMessage message) {
        switch (message.getEventType()) {
            case "ORDER_PLACED"         -> sendOrderPlaced(message);
            case "PAYMENT_SUCCESS"      -> sendPaymentSuccess(message);
            case "PAYMENT_FAILED"       -> sendPaymentFailed(message);
            case "ORDER_STATUS_UPDATED" -> sendStatusUpdate(message);
            default -> log.warn("Unhandled event type: {}", message.getEventType());
        }
    }

    private void sendOrderPlaced(NotificationMessage msg) {
        // Replace with JavaMailSender / AWS SES / Twilio SDK calls
        log.info("[EMAIL] To userId={} | Subject: Order Confirmed | Body: Your order has been placed successfully.", msg.getUserId());
        log.info("[PUSH]  To userId={} | Your order is being processed!", msg.getUserId());
    }

    private void sendPaymentSuccess(NotificationMessage msg) {
        log.info("[EMAIL] To userId={} | Subject: Payment Successful | Body: {}", msg.getUserId(), msg.getBody());
        log.info("[SMS]   To userId={} | Payment successful. {}", msg.getUserId(), msg.getBody());
        log.info("[PUSH]  To userId={} | Payment confirmed!", msg.getUserId());
    }

    private void sendPaymentFailed(NotificationMessage msg) {
        log.info("[EMAIL] To userId={} | Subject: Payment Failed | Body: {}", msg.getUserId(), msg.getBody());
        log.info("[PUSH]  To userId={} | Payment failed. Please retry.", msg.getUserId());
    }

    private void sendStatusUpdate(NotificationMessage msg) {
        log.info("[PUSH]  To userId={} | Order status updated: {}", msg.getUserId(), msg.getBody());
    }
}
