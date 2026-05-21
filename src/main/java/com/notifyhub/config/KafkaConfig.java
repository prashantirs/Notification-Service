package com.notifyhub.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String EMAIL_TOPIC   = "notification.email";
    public static final String SMS_TOPIC     = "notification.sms";
    public static final String PUSH_TOPIC    = "notification.push";
    public static final String RETRY_TOPIC   = "notification.retry";
    public static final String DLQ_TOPIC     = "notification.dlq";

    @Bean
    public NewTopic emailTopic() {
       return TopicBuilder.name(EMAIL_TOPIC).partitions(1).replicas(1).build();
    }


    @Bean
    public NewTopic smsTopic() {
        return TopicBuilder.name(SMS_TOPIC).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pushTopic() {
        return TopicBuilder.name(PUSH_TOPIC).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic retryTopic() {
        return TopicBuilder.name(RETRY_TOPIC).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic dlqTopic() {
        return TopicBuilder.name(DLQ_TOPIC).partitions(1).replicas(1).build();
    }
}
