package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.dto.CreateOrderRequest;
import com.spring.boot.levelup.payment.entity.order.Order;
import com.spring.boot.levelup.payment.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(
                "ORD-" + UUID.randomUUID(),
                request.userId(),
                request.amount(),
                request.currency(),
                request.description()
        );

        return orderRepository.save(order);
    }
}