package com.kevin.microservices.orderService.service;

import com.kevin.microservices.orderService.client.InventoryClient;
import com.kevin.microservices.orderService.dto.OrderRequest;
import com.kevin.microservices.orderService.event.OrderPlacedEvent;
import com.kevin.microservices.orderService.model.Order;
import com.kevin.microservices.orderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {

        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (isProductInStock || true) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);


            OrderRequest orderDetails = new OrderRequest(12345L,"ORD123456","SKU1",new BigDecimal("47"),2,new OrderRequest.UserDetails("kevin47@gmail.com","Kevin","Kevin"));

            order = new Order(null,"ORD123456","SKU12345",new BigDecimal("299.99"),2);

            // Send the message to Kafka Topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderDetails.userDetails().email());
            orderPlacedEvent.setFirstName(orderDetails.userDetails().firstName());
            orderPlacedEvent.setLastName(orderDetails.userDetails().lastName());
            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
        } else {
            throw new RuntimeException("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
        }
    }
}
