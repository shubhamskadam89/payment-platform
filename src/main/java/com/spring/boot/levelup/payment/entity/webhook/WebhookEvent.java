package com.spring.boot.levelup.payment.entity.webhook;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "webhook_events")
public class WebhookEvent {

    private static final int MAX_RETRY = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    @Column(name = "event_id", unique = true, nullable = false)
    private String eventId;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    private boolean success;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    private WebhookEventStatus status = WebhookEventStatus.PENDING;

    private int retryCount = 0;

    private LocalDateTime lastAttemptAt;

    private LocalDateTime nextRetryAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    protected WebhookEvent() {}

    public WebhookEvent(String provider,
                        String eventId,
                        String idempotencyKey,
                        String providerPaymentId,
                        boolean success,
                        String payload) {

        this.provider = provider;
        this.eventId = eventId;
        this.idempotencyKey = idempotencyKey;
        this.providerPaymentId = providerPaymentId;
        this.success = success;
        this.payload = payload;
        this.nextRetryAt = LocalDateTime.now();
    }

    public boolean canRetry() {
        return retryCount < MAX_RETRY;
    }

    public void markProcessed() {
        this.status = WebhookEventStatus.PROCESSED;
    }

    public void markFailed() {
        this.status = WebhookEventStatus.FAILED;
    }

    public void registerFailure() {
        this.retryCount++;
        this.lastAttemptAt = LocalDateTime.now();

        // Exponential backoff: 2^retryCount seconds
        int delaySeconds = (int) Math.pow(2, retryCount);
        this.nextRetryAt = LocalDateTime.now().plusSeconds(delaySeconds);

        if (!canRetry()) {
            this.status = WebhookEventStatus.FAILED;
        }
    }

}