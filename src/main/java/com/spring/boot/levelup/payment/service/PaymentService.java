package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.entity.order.Order;
import com.spring.boot.levelup.payment.entity.order.OrderStatus;
import com.spring.boot.levelup.payment.entity.payment.Payment;
import com.spring.boot.levelup.payment.entity.payment.PaymentProvider;
import com.spring.boot.levelup.payment.entity.payment.PaymentStatus;
import com.spring.boot.levelup.payment.repository.OrderRepository;
import com.spring.boot.levelup.payment.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private static final boolean SIMULATE_FAILURE = true; //temp

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Payment createPayment(Long orderId,
                                 PaymentProvider provider) {

        log.info("START createPayment | orderId={} | provider={}", orderId, provider);

        String idempotencyKey = UUID.randomUUID().toString();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found | orderId={}", orderId);
                    return new IllegalArgumentException("Order not found");
                });

        log.info("Loaded order | orderId={} | status={}", order.getId(), order.getStatus());

        if (order.getStatus() != OrderStatus.CREATED &&
                order.getStatus() != OrderStatus.PAYMENT_PENDING) {

            log.warn("Invalid state for payment | orderId={} | status={}",
                    orderId, order.getStatus());

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

        paymentRepository.save(payment);

        log.info("Payment created | paymentId={} | idempotencyKey={}",
                payment.getId(), idempotencyKey);

        log.info("END createPayment | orderId={}", orderId);

        return payment;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentSuccess(String providerPaymentId,
                                     String idempotencyKey,
                                     String rawResponse) {

        log.info("START handlePaymentSuccess | idempotencyKey={} | providerPaymentId={}",
                idempotencyKey, providerPaymentId);

        if (SIMULATE_FAILURE) {
            throw new RuntimeException("Simulated failure");
        }

        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> {
                    log.error("Payment not found | idempotencyKey={}", idempotencyKey);
                    return new IllegalArgumentException("Payment not found");
                });

        log.info("Loaded payment | paymentId={} | currentStatus={}",
                payment.getId(), payment.getStatus());

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Idempotent skip (already SUCCESS) | idempotencyKey={}", idempotencyKey);
            return;
        }

        log.debug("Raw success payload | idempotencyKey={} | payload={}",
                idempotencyKey, rawResponse);

        payment.markSuccess(providerPaymentId, rawResponse);

        log.info("Payment marked SUCCESS | paymentId={}", payment.getId());

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order not found during success | orderId={}", payment.getOrderId());
                    return new IllegalStateException("Order not found");
                });

        log.info("Loaded order | orderId={} | currentStatus={}",
                order.getId(), order.getStatus());

        order.markPaid();

        log.info("Order marked PAID | orderId={}", order.getId());

        log.info("END handlePaymentSuccess | idempotencyKey={}", idempotencyKey);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentFailure(String idempotencyKey,
                                     String reason,
                                     String rawResponse) {

        log.warn("START handlePaymentFailure | idempotencyKey={} | reason={}",
                idempotencyKey, reason);

        if (SIMULATE_FAILURE) {
            throw new RuntimeException("Simulated failure");
        }

        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> {
                    log.error("Payment not found | idempotencyKey={}", idempotencyKey);
                    return new IllegalArgumentException("Payment not found");
                });

        log.info("Loaded payment | paymentId={} | currentStatus={}",
                payment.getId(), payment.getStatus());

        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.info("Idempotent skip (already FAILED) | idempotencyKey={}", idempotencyKey);
            return;
        }

        log.debug("Raw failure payload | idempotencyKey={} | payload={}",
                idempotencyKey, rawResponse);

        payment.markFailed(reason, rawResponse);

        log.warn("Payment marked FAILED | paymentId={}", payment.getId());

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order not found during failure | orderId={}", payment.getOrderId());
                    return new IllegalStateException("Order not found");
                });

        log.info("Loaded order | orderId={} | currentStatus={}",
                order.getId(), order.getStatus());

        order.markFailed();

        log.warn("Order marked FAILED | orderId={}", order.getId());

        log.warn("END handlePaymentFailure | idempotencyKey={}", idempotencyKey);
    }
}