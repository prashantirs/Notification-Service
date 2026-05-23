package com.notifyhub.controller;

import com.notifyhub.dto.NotificationRequest;
import com.notifyhub.entity.NotificationLog;
import com.notifyhub.kafka.NotificationProducer;
import com.notifyhub.repository.NotificationRepository;
import com.notifyhub.service.RateLimiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationProducer producer;
    private final NotificationRepository repository;
    private final RateLimiterService rateLimiter;

    // Send single notification
    @PostMapping("/notify")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody NotificationRequest request) {

        // ← Rate limit check BEFORE publishing to Kafka
        if (!rateLimiter.isAllowed(request.getRecipient(),
                request.getChannel())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)   // 429 status code for rate limit exceeded
                    .body("Rate limit exceeded! Max " + 5 + " notifications per minute for: " + request.getRecipient());
        }
        log.info("📨 New notification request for: {}", request.getRecipient());
        producer.sendNotification(request);
        return ResponseEntity.ok("Notification queued for: " + request.getRecipient());
    }

    // Send bulk notifications
    @PostMapping("/notify/bulk")
    public ResponseEntity<String> sendBulk(@RequestBody List<NotificationRequest> requests) {

        requests.forEach(producer::sendNotification);
        return ResponseEntity.ok("Bulk notifications queued: " + requests.size());
    }

    // Get all notifications
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationLog>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    // Get by status
    @GetMapping("/notifications/status/{status}")
    public ResponseEntity<List<NotificationLog>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(repository.findByStatus(status.toUpperCase()));
    }

    // Get stats
    @GetMapping("/notifications/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "byStatus",  repository.countByStatus(),
                        "byChannel", repository.countByChannel()
                )
        );
    }
}