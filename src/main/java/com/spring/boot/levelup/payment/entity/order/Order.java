package com.spring.boot.levelup.payment.entity.order;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_reference", nullable = false, unique = true)
    private String orderReference;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long amount; // smallest currency unit

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false,columnDefinition = "order_status")
    private OrderStatus status;

    @Version
    @Column(nullable = false)
    private Integer version;

    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ===== Constructors ===== */

    protected Order() {
    }

    public Order(String orderReference, Long userId, Long amount, String currency, String description) {
        this.orderReference = orderReference;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.status = OrderStatus.CREATED;
    }

    /* ===== Domain Methods ===== */

    public void markPaymentPending() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Invalid state transition");
        }
        this.status = OrderStatus.PAYMENT_PENDING;
    }

    public void markPaid() {
        if (this.status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Order cannot be marked as PAID");
        }
        this.status = OrderStatus.PAID;
    }

    public void markFailed() {
        if (this.status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Order cannot be marked as FAILED");
        }
        this.status = OrderStatus.FAILED;
    }

    /* ===== Getters ===== */

    public Long getId() {
        return id;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Integer getVersion() {
        return version;
    }
}
