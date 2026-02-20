package com.spring.boot.levelup.payment.dto.payment;

import com.spring.boot.levelup.payment.entity.order.OrderStatus;

public record OrderResponse(
        Long id,
        String orderReference,
        Long userId,
        Long amount,
        String currency,
        OrderStatus status
) {}
