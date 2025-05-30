package com.demo.webchat;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);

    private final StringRedisTemplate redisTemplate;
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public ChatHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        redisTemplate.opsForSet().add("chat:sessions", session.getId());
        logger.info("Client connected: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        long startTime = System.nanoTime();

        String rawPayload = message.getPayload();
        String timestampedPayload = "[" + LocalDateTime.now() + "] " + rawPayload;

        redisTemplate.opsForList().rightPush("chat:messages", timestampedPayload);

        long endTime = System.nanoTime();
        long elapsedNano = endTime - startTime;
        double elapsedMillis = elapsedNano / 1_000_000.0;
        long elapsedMicros = elapsedNano / 1_000;

        String formattedLatency = String.format("%.3f ms (%d us)", elapsedMillis, elapsedMicros);
        String payloadWithTime = timestampedPayload + " (Redis processing time: " + formattedLatency + ")";
        logger.info("Message received and processed in {}: {}", formattedLatency, rawPayload);

        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payloadWithTime));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        redisTemplate.opsForSet().remove("chat:sessions", session.getId());
        logger.info("Client disconnected: {}", session.getId());
    }
}
