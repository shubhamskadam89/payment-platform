package com.spring.boot.levelup.payment.entity.order;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    FAILED,
    CANCELLED,
    REFUNDED,
    PARTIALLY_REFUNDED
}
