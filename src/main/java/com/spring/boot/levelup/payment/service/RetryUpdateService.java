package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.entity.webhook.WebhookEvent;
import com.spring.boot.levelup.payment.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetryUpdateService {

    private final WebhookEventRepository repository;

    public RetryUpdateService(WebhookEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAfterFailure(Long eventId) {

        WebhookEvent event = repository.findById(eventId)
                .orElseThrow();

        event.registerFailure();

        repository.save(event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markProcessed(Long eventId) {

        WebhookEvent event = repository.findById(eventId)
                .orElseThrow();

        event.markProcessed();

        repository.save(event);
    }
}