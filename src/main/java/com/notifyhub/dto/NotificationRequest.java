package com.notifyhub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {

    @NotBlank(message = "Recipient is required")
    private String recipient;     // email or phone

    @NotBlank(message = "Channel is required")
    private String channel;       // EMAIL, SMS, PUSH

    private String subject;       // for email

    @NotBlank(message = "Message is required")
    private String message;
}