package com.notifyhub.kafka;

import com.notifyhub.config.KafkaConfig;
import com.notifyhub.dto.NotificationRequest;
import com.notifyhub.entity.NotificationLog;
import com.notifyhub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;

    @KafkaListener(topics = KafkaConfig.EMAIL_TOPIC, groupId = "notification-group")
    public void consumeEmail(NotificationRequest request) {
        log.info("📧 EMAIL received for: {}", request.getRecipient());
        processNotification(request, "EMAIL");
    }

    @KafkaListener(topics = KafkaConfig.SMS_TOPIC, groupId = "notification-group")
    public void consumeSms(NotificationRequest request) {
        log.info("📱 SMS received for: {}", request.getRecipient());
        processNotification(request, "SMS");
    }

    @KafkaListener(topics = KafkaConfig.PUSH_TOPIC, groupId = "notification-group")
    public void consumePush(NotificationRequest request) {
        log.info("🔔 PUSH received for: {}", request.getRecipient());
        processNotification(request, "PUSH");
    }

    @KafkaListener(topics = KafkaConfig.RETRY_TOPIC, groupId = "notification-group")
    public void consumeRetry(NotificationRequest request) {
        log.warn("🔁 RETRY attempt #{} for: {}",
                request.getRetryCount(), request.getRecipient());

        if (request.getRetryCount() >= 3) {
            log.error("❌ Max retries reached. Sending to DLQ: {}",
                    request.getRecipient());
            updateStatus(request, "FAILED");
            notificationProducer.sendToDlq(request);
            return;
        }

        request.setRetryCount(request.getRetryCount() + 1);
        processNotification(request, request.getChannel());
    }

    @KafkaListener(topics = KafkaConfig.DLQ_TOPIC, groupId = "notification-group")
    public void consumeDlq(NotificationRequest request) {
        log.error("💀 DLQ - Permanently failed for: {}",
                request.getRecipient());
        updateStatus(request, "DEAD");
    }

    // ─── Private Helpers ───────────────────────────────────────

    private void processNotification(NotificationRequest request,
                                     String channel) {
        NotificationLog log_entry = saveLog(request, channel, "PENDING");

        try {
            // Simulate processing (email sending comes in next step)
            simulateSend(channel, request);

            log_entry.setStatus("SENT");
            notificationRepository.save(log_entry);
            log.info("✅ {} sent successfully to: {}", channel, request.getRecipient());

        } catch (Exception e) {
            log.error("❌ Failed to send {} to {}: {}", channel, request.getRecipient(), e.getMessage());
            log_entry.setStatus("FAILED");
            notificationRepository.save(log_entry);

            // Send to retry topic
            notificationProducer.sendToRetry(request);
        }
    }

    private NotificationLog saveLog(NotificationRequest request,
                                    String channel, String status) {
        NotificationLog entry = new NotificationLog();
        entry.setRecipient(request.getRecipient());
        entry.setChannel(channel);
        entry.setSubject(request.getSubject());
        entry.setMessage(request.getMessage());
        entry.setStatus(status);
        entry.setRetryCount(request.getRetryCount());
        return notificationRepository.save(entry);
    }

    private void updateStatus(NotificationRequest request, String status) {
        notificationRepository.findByStatus("PENDING")
                .stream()
                .filter(n -> n.getRecipient().equals(request.getRecipient()))
                .findFirst()
                .ifPresent(n -> {
                    n.setStatus(status);
                    notificationRepository.save(n);
                });
    }

    private void simulateSend(String channel,
                              NotificationRequest request) {
        // Real email sending will be added next step
        // SMS and PUSH are mocked
        log.info("📤 Simulating {} send to {}", channel, request.getRecipient());
    }
}