package com.spring.boot.levelup.payment.dto.payment;

import com.spring.boot.levelup.payment.entity.payment.PaymentProvider;
import com.spring.boot.levelup.payment.entity.payment.PaymentStatus;

public record PaymentResponse(
        Long id,
        Long orderId,
        PaymentProvider provider,
        String idempotencyKey,
        PaymentStatus status
) {}