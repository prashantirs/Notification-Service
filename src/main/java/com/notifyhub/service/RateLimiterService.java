package com.notifyhub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public boolean isAllowed(String recipient, String channel) {
        String key = "rate:" + recipient + ":" + channel;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            // First request — set expiry of 1 minute
            redisTemplate.expire(key, WINDOW);
        }

        if (count > MAX_REQUESTS) {
            log.warn("🚫 Rate limit exceeded for [{}] on channel [{}]" +
                    " — count: {}", recipient, channel, count);
            return false;
        }

        log.info("✅ Rate check passed for [{}] — {}/{} requests", recipient, count, MAX_REQUESTS);
        return true;
    }
}