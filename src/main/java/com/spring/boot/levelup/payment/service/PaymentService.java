package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.entity.order.Order;
import com.spring.boot.levelup.payment.entity.order.OrderStatus;
import com.spring.boot.levelup.payment.entity.payment.Payment;
import com.spring.boot.levelup.payment.entity.payment.PaymentProvider;
import com.spring.boot.levelup.payment.entity.payment.PaymentStatus;
import com.spring.boot.levelup.payment.repository.OrderRepository;
import com.spring.boot.levelup.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository,
            OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Payment createPayment(Long orderId,
            PaymentProvider provider) {

        String idempotencyKey = UUID.randomUUID().toString();

        // Load Order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED &&
                order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Order cannot accept new payment");
        }

        order.markPaymentPending();

        Payment payment = new Payment(
                orderId,
                provider,
                idempotencyKey,
                order.getAmount(),
                order.getCurrency()
        );

        return paymentRepository.save(payment);
    }

    @Transactional
    public void handlePaymentSuccess(String providerPaymentId,
            String idempotencyKey,
            String rawResponse) {

        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return; // idempotent protection
        }

        payment.markSuccess(providerPaymentId, rawResponse);

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        order.markPaid();
    }

    @Transactional
    public void handlePaymentFailure(String idempotencyKey,
            String reason,
            String rawResponse) {

        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        payment.markFailed(reason, rawResponse);

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        order.markFailed();
    }
}
