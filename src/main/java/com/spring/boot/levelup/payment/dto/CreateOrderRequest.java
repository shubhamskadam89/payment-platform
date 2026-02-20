package com.spring.boot.levelup.payment.dto;

import jakarta.validation.constraints.*;

public record CreateOrderRequest(
        @NotNull Long userId,
        @NotNull @Positive Long amount,
        @NotBlank String currency,
        String description
) {}