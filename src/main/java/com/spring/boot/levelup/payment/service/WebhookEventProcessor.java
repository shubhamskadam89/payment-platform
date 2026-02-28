package com.spring.boot.levelup.payment.service;

import com.spring.boot.levelup.payment.entity.webhook.WebhookEvent;
import com.spring.boot.levelup.payment.entity.webhook.WebhookEventStatus;
import com.spring.boot.levelup.payment.repository.WebhookEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class WebhookEventProcessor {

    private final WebhookEventRepository webhookRepository;
    private final WebhookEventHandler webhookEventHandler;

    public WebhookEventProcessor(WebhookEventRepository webhookRepository,
                                 WebhookEventHandler webhookEventHandler) {
        this.webhookRepository = webhookRepository;
        this.webhookEventHandler = webhookEventHandler;
    }

    @Scheduled(fixedDelay = 5000)
    public void processUnprocessedEvents() {

        List<WebhookEvent> events = webhookRepository.findAll()
                .stream()
                .filter(e -> e.getStatus() == WebhookEventStatus.PENDING)
                .filter(e -> e.getNextRetryAt() == null ||
                        e.getNextRetryAt().isBefore(LocalDateTime.now()))
                .toList();

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} pending webhook events to process", events.size());

        for (WebhookEvent event : events) {
            try {
                webhookEventHandler.process(event);
            } catch (Exception ignored) {
                // Failure already handled inside handler
            }
        }
    }
}