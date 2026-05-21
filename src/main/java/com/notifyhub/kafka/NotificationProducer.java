package com.notifyhub.kafka;

import com.notifyhub.config.KafkaConfig;
import com.notifyhub.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;


    public void sendNotification(NotificationRequest request) {
        String topic = switch (request.getChannel().toUpperCase()) {
            case "EMAIL" -> KafkaConfig.EMAIL_TOPIC;
            case "SMS"   -> KafkaConfig.SMS_TOPIC;
            case "PUSH"  -> KafkaConfig.PUSH_TOPIC;
            default      -> throw new IllegalArgumentException("Unknown channel: " + request.getChannel());
        };

        log.info("Publishing to topic [{}] for recipient [{}]", topic, request.getRecipient());

        kafkaTemplate.send(topic, request.getRecipient(), request);
    }

    public void sendToRetry(NotificationRequest request) {
        log.info("Sending to RETRY topic for recipient [{}]", request.getRecipient());
        kafkaTemplate.send(KafkaConfig.RETRY_TOPIC, request.getRecipient(), request);
    }

    public void sendToDlq(NotificationRequest request) {
        log.warn("Sending to DLQ for recipient [{}]", request.getRecipient());
        kafkaTemplate.send(KafkaConfig.DLQ_TOPIC, request.getRecipient(), request);
    }
}
