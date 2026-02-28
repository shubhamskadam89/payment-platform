package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.entity.webhook.WebhookEvent;
import com.spring.boot.levelup.payment.entity.webhook.WebhookEventStatus;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebhookEventHandler {

    private final PaymentService paymentService;
    private final RetryUpdateService retryUpdateService;

    public WebhookEventHandler(PaymentService paymentService,
                               RetryUpdateService retryUpdateService) {
        this.paymentService = paymentService;
        this.retryUpdateService = retryUpdateService;
    }


    public void process(WebhookEvent event) {

        try {

            if (event.isSuccess()) {
                paymentService.handlePaymentSuccess(
                        event.getProviderPaymentId(),
                        event.getIdempotencyKey(),
                        event.getPayload()
                );
            } else {
                paymentService.handlePaymentFailure(
                        event.getIdempotencyKey(),
                        "Provider failure",
                        event.getPayload()
                );
            }

            retryUpdateService.markProcessed(event.getId());

        } catch (Exception ex) {

            retryUpdateService.updateAfterFailure(event.getId());

        }
    }
}