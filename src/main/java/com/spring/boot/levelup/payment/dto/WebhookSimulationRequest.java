package com.spring.boot.levelup.payment.dto;

public record WebhookSimulationRequest(
        String idempotencyKey,
        String providerPaymentId,
        String rawResponse) {
}
