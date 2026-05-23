package com.notifyhub.service;

import com.notifyhub.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getRecipient());
            message.setSubject(request.getSubject() != null ? request.getSubject()
                    : "Notification from NotifyHub");
            message.setText(request.getMessage());

            mailSender.send(message);
            log.info("✅ Email sent to: {}", request.getRecipient());

        } catch (Exception e) {
            log.error("❌ Email failed to {}: {}", request.getRecipient(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}