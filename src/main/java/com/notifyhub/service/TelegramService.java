package com.notifyhub.service;

import com.notifyhub.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(NotificationRequest request) {
        try {
            log.info("Your Telegram Bot Token: {}", botToken);
            String text = "🔔 *NotifyHub Alert*\n" + "To: " + request.getRecipient() + "\n" + "Subject: " + request.getSubject() + "\n" + "Message: " + request.getMessage();

            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + encode(text) + "&parse_mode=Markdown";

            restTemplate.getForObject(url, String.class);
            log.info("✅ Telegram notification sent to: {}", request.getRecipient());

        } catch (Exception e) {
            log.error("❌ Telegram failed: {}", e.getMessage());
            throw new RuntimeException("Telegram sending failed", e);
        }
    }

    private String encode(String text) {
        return text.replace(" ", "%20")
                .replace("\n", "%0A")
                .replace("*", "%2A");
    }
}