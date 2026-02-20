package com.spring.boot.levelup.payment.entity.payment;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "payments")

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false,columnDefinition = "payment_provider")
    private PaymentProvider provider;

    @Column(name = "provider_order_id")
    private String providerOrderId;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false,columnDefinition = "payment_status")
    private PaymentStatus status;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_response", columnDefinition = "jsonb")
    private String rawResponse;

    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Payment() {
    }

    public Payment(Long orderId,
            PaymentProvider provider,
            String idempotencyKey,
            Long amount,
            String currency) {

        this.orderId = orderId;
        this.provider = provider;
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.INITIATED;
    }

    /* ===== Domain Methods ===== */

    public void markRequiresAction(String rawResponse) {
        this.status = PaymentStatus.REQUIRES_ACTION;
        this.rawResponse = rawResponse;
    }

    public void markSuccess(String providerPaymentId, String rawResponse) {
        this.status = PaymentStatus.SUCCESS;
        this.providerPaymentId = providerPaymentId;
        this.rawResponse = rawResponse;
    }

    public void markFailed(String reason, String rawResponse) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.rawResponse = rawResponse;
    }

    /* ===== Getters ===== */

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
