package com.spring.boot.levelup.payment.repository;

import com.spring.boot.levelup.payment.entity.webhook.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent,Long> {

    Optional<WebhookEvent> findByEventId(String eventId);
}
