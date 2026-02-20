package com.spring.boot.levelup.payment.controller;

import com.spring.boot.levelup.payment.mappers.PaymentMapper;
import com.spring.boot.levelup.payment.dto.CreateOrderRequest;
import com.spring.boot.levelup.payment.dto.CreatePaymentRequest;
import com.spring.boot.levelup.payment.dto.WebhookSimulationRequest;
import com.spring.boot.levelup.payment.dto.payment.OrderResponse;
import com.spring.boot.levelup.payment.dto.payment.PaymentResponse;
import com.spring.boot.levelup.payment.entity.order.Order;
import com.spring.boot.levelup.payment.entity.payment.Payment;
import com.spring.boot.levelup.payment.service.OrderService;
import com.spring.boot.levelup.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController

@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService,
            PaymentMapper paymentMapper, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.paymentMapper = paymentMapper;

    }

    /* ===== 1️⃣ Create Order ===== */

    @PostMapping("/orders")
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {

        Order saved = orderService.createOrder(request);
        return paymentMapper.mapToOrderResponse(saved);
    }

    /* ===== 2️⃣ Create Payment Attempt ===== */

    @PostMapping("/create")
    public PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {

        Payment payment = paymentService.createPayment(
                request.orderId(),
                request.provider()

        );

        return paymentMapper.mapToPaymentResponse(payment);
    }

    /* ===== 3️⃣ Simulate Success ===== */

    @PostMapping("/simulate-success")
    public void simulateSuccess(@RequestBody WebhookSimulationRequest request) {
        paymentService.handlePaymentSuccess(
                request.providerPaymentId(),
                request.idempotencyKey(),
                request.rawResponse());
    }

    /* ===== 4️⃣ Simulate Failure ===== */

    @PostMapping("/simulate-failure")
    public void simulateFailure(@RequestBody WebhookSimulationRequest request) {
        paymentService.handlePaymentFailure(
                request.idempotencyKey(),
                "Simulated Failure",
                request.rawResponse());
    }
}
