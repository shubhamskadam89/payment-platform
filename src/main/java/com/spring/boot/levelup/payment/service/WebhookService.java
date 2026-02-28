package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.entity.webhook.WebhookEvent;
import com.spring.boot.levelup.payment.repository.WebhookEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentService paymentService;
    private final WebhookEventRepository webhookRepo;

    @Transactional
    public void handleWebhookEvent(String provider,
                                   String eventId,
                                   String idempotencyKey,
                                   String providerPaymentId,
                                   String payload,
                                   boolean success){
        log.info("Received webhook | provider={} | eventId={}", provider, eventId);

        //IdempotencyKey check
        if(webhookRepo.findByEventId(eventId).isPresent()){
            log.info("Duplicate webhook ignored | eventID={}",eventId);
            return;
        }
        WebhookEvent event = new WebhookEvent(provider,eventId,idempotencyKey
                                                 ,providerPaymentId,success,payload);
        webhookRepo.save(event);

        log.info("Webhook persisted | eventId={}", eventId);

    }
}
