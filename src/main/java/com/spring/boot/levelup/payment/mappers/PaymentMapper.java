package com.spring.boot.levelup.payment.mappers;

import com.spring.boot.levelup.payment.dto.payment.OrderResponse;
import com.spring.boot.levelup.payment.dto.payment.PaymentResponse;
import com.spring.boot.levelup.payment.entity.order.Order;
import com.spring.boot.levelup.payment.entity.payment.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {
    public OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderReference(),
                order.getUserId(),
                order.getAmount(),
                order.getCurrency(),
                order.getStatus()
        );
    }

    public PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getProvider(),
                payment.getIdempotencyKey(),
                payment.getStatus()
        );
    }
}
