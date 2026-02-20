package com.spring.boot.levelup.payment.entity.payment;

public enum PaymentStatus {
    INITIATED,
    REQUIRES_ACTION,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED
}
