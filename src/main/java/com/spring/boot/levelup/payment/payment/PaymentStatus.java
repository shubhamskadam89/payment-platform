package com.spring.boot.levelup.payment.payment;

public enum PaymentStatus {
    INITIATED,
    REQUIRES_ACTION,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED
}
