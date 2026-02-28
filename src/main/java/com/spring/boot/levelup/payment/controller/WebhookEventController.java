package com.spring.boot.levelup.payment.controller;

import com.spring.boot.levelup.payment.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookEventController {

    private final WebhookService webhookService;

    @PostMapping("/stripe")
    public void stripeWebhook(@RequestBody String payload,
                              @RequestParam String eventId,
                              @RequestParam String idempotencyKey,
                              @RequestParam(required = false) String providerPaymentId,
                              @RequestParam boolean success) {
        webhookService.handleWebhookEvent("STRIPE",
                eventId,
                idempotencyKey,
                providerPaymentId,
                payload,
                success);
    }

    @PostMapping("/razorpay")
    public void razorpayWebhook(@RequestBody String payload,
                              @RequestParam String eventId,
                              @RequestParam String idempotencyKey,
                              @RequestParam(required = false) String providerPaymentId,
                              @RequestParam boolean success) {
        webhookService.handleWebhookEvent("RAZORPAY",
                eventId,
                idempotencyKey,
                providerPaymentId,
                payload,
                success);
    }


}
