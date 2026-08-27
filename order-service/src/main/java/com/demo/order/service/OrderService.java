package com.demo.order.service;

import com.demo.order.client.ProductServiceClient;
import com.demo.order.client.UserServiceClient;
import com.demo.order.dto.OrderDtos;
import com.demo.order.event.OrderEvent;
import com.demo.order.model.Order;
import com.demo.order.model.OrderItem;
import com.demo.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository        orderRepository;
    private final UserServiceClient      userServiceClient;
    private final ProductServiceClient   productServiceClient;
    //private final SnsClient              snsClient;
    private final ObjectMapper           objectMapper;

//    @Value("${aws.sns.order-topic-arn}")
//    private String orderTopicArn;

    @Transactional
    public OrderDtos.OrderResponse createOrder(String userId, OrderDtos.CreateOrderRequest request) {

        // Step 1: Validate user via WebClient -> user-service (Eureka lb)
        UserServiceClient.UserDto user = userServiceClient.getUserById(userId);
        if (user == null) throw new RuntimeException("User not found: " + userId);
        log.info("Order by user: {} ({})", user.name(), user.email());

        // Step 2: Validate product + stock via WebClient -> product-service (Eureka lb)
        List<OrderItem> items = request.getItems().stream().map(req -> {
            ProductServiceClient.ProductDto product =
                    productServiceClient.getProductById(req.getProductId());

            if (product == null)
                throw new RuntimeException("Product not found: " + req.getProductId());
            if (!product.active())
                throw new RuntimeException("Product unavailable: " + req.getProductId());
            if (product.stock() < req.getQuantity())
                throw new RuntimeException("Insufficient stock for: " + product.name()
                        + ". Available: " + product.stock());

            BigDecimal unitPrice = product.price(); // always use server-side price
            return OrderItem.builder()
                    .productId(product.id())
                    .productName(product.name())
                    .quantity(req.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(unitPrice.multiply(BigDecimal.valueOf(req.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

        // Step 3: Save order
        BigDecimal total = items.stream().map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order saved = orderRepository.save(Order.builder()
                .userId(userId).items(items).totalAmount(total)
                .shippingAddress(request.getShippingAddress())
                .status(Order.OrderStatus.PENDING).build());

        log.info("Order {} saved — total {}", saved.getId(), total);

        // Step 4: Decrement stock via WebClient -> product-service
        items.forEach(i -> productServiceClient.decrementStock(i.getProductId(), i.getQuantity()));

        // Step 5: Publish to SNS -> fans out to SQS queues
        publishOrderEvent(saved, "ORDER_PLACED");
         // save the update date in db
        //this my modification code 
        //this my modification code
        //second coding change by the durga prasad
        //thrid time code change by the prasad
        return mapToResponse(saved);
    }

    public OrderDtos.OrderResponse getOrder(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        if (!order.getUserId().equals(userId)) throw new RuntimeException("Access denied");
        return mapToResponse(order);
    }

    public List<OrderDtos.OrderResponse> getUserOrders(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderDtos.OrderResponse updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(Order.OrderStatus.valueOf(status));
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        publishOrderEvent(updated, "ORDER_STATUS_UPDATED");
        return mapToResponse(updated);
    }

    private void publishOrderEvent(Order order, String eventType) {
        try {
            List<OrderEvent.OrderItemEvent> itemEvents = order.getItems().stream()
                    .map(i -> OrderEvent.OrderItemEvent.builder()
                            .productId(i.getProductId()).productName(i.getProductName())
                            .quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).build())
                    .collect(Collectors.toList());

            String message = objectMapper.writeValueAsString(OrderEvent.builder()
                    .eventType(eventType).orderId(order.getId()).userId(order.getUserId())
                    .totalAmount(order.getTotalAmount()).status(order.getStatus().name())
                    .shippingAddress(order.getShippingAddress()).items(itemEvents)
                    .timestamp(LocalDateTime.now()).build());

//            snsClient.publish(PublishRequest.builder()
//                    .topicArn(orderTopicArn).message(message).subject(eventType)
//                    .messageAttributes(Map.of("eventType", MessageAttributeValue.builder()
//                            .dataType("String").stringValue(eventType).build()))
//                    .build());
            log.info("SNS published: {} for order {}", eventType, order.getId());
        } catch (Exception e) {
            log.error("SNS publish failed: {}", e.getMessage());
        }
    }

    private OrderDtos.OrderResponse mapToResponse(Order order) {
        OrderDtos.OrderResponse res = new OrderDtos.OrderResponse();
        res.setId(order.getId()); res.setUserId(order.getUserId());
        res.setTotalAmount(order.getTotalAmount()); res.setStatus(order.getStatus().name());
        res.setShippingAddress(order.getShippingAddress()); res.setCreatedAt(order.getCreatedAt());
        res.setItems(order.getItems().stream().map(item -> {
            OrderDtos.OrderItemResponse ir = new OrderDtos.OrderItemResponse();
            ir.setProductId(item.getProductId()); ir.setProductName(item.getProductName());
            ir.setQuantity(item.getQuantity()); ir.setUnitPrice(item.getUnitPrice());
            ir.setSubtotal(item.getSubtotal()); return ir;
        }).collect(Collectors.toList()));
        return res;
    }
}
