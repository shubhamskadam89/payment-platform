package com.spring.boot.levelup.payment.repository;

import com.spring.boot.levelup.payment.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderReference(String orderReference);
}
