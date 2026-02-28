package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.dto.CreateOrderRequest;
import com.spring.boot.levelup.payment.entity.order.Order;
import com.spring.boot.levelup.payment.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(CreateOrderRequest request) {

        log.info("Creating order | userId={} | amount={} | currency={}",
                request.userId(),
                request.amount(),
                request.currency());

        Order order = new Order(
                "ORD-" + UUID.randomUUID(),
                request.userId(),
                request.amount(),
                request.currency(),
                request.description()
        );

        Order saved = orderRepository.save(order);

        log.info("Order created | orderId={} | reference={}",
                saved.getId(),
                saved.getOrderReference());

        return saved;
    }
}