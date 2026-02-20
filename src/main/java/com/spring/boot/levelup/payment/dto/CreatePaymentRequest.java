package com.spring.boot.levelup.payment.dto;

import com.spring.boot.levelup.payment.entity.payment.PaymentProvider;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull Long orderId,
        @NotNull PaymentProvider provider
) {}