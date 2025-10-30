package com.support_svc.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.support_svc.model.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageCacheServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(MessageCacheServiceImpl.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveMessage(Message message) {

        try {
            String messageKey = buildKey(message.getId());
            String json = objectMapper.writeValueAsString(message);

            redisTemplate.opsForValue().set(messageKey, json);

            redisTemplate.opsForSet().add("messages:all", messageKey);

            String caseKey = "case:" + message.getCaseId() + ":messages";
            redisTemplate.opsForSet().add(caseKey, messageKey);

            log.info("Saved message [{}] to Redis under case [{}]", message.getId(), message.getCaseId());
        } catch (Exception e) {
            log.error("Failed to save message [{}] to Redis", message.getId(), e);
        }
    }

    public Message getMessage(UUID id) {

        try {
            String key = buildKey(id);
            String json = redisTemplate.opsForValue().get(key);

            if (json == null) {
                log.info("Cache miss for message [{}]", id);
                return null;
            }

            log.info("Cache hit for message [{}]", id);

            JsonNode node = objectMapper.readTree(json);

            return objectMapper.treeToValue(node, Message.class);
        } catch (Exception e) {
            log.error("Failed to read message [{}] from Redis", id, e);
            return null;
        }
    }

    public List<Message> getAllMessagesByCase(UUID caseId) {

        try {
            String caseKey = "case:" + caseId + ":messages";
            var messageKeys = redisTemplate.opsForSet().members(caseKey);

            if (messageKeys == null || messageKeys.isEmpty()) {
                log.info("No messages found in Redis for case [{}]", caseId);
                return List.of();
            }

            return messageKeys.stream()
                    .map(this::getMessageJson)
                    .flatMap(Optional::stream)
                    .map(this::parseMessage)
                    .flatMap(Optional::stream)
                    .sorted(Comparator.comparing(Message::getDateTime))
                    .toList();

        } catch (Exception e) {
            log.error("Failed to read messages for case [{}] from Redis", caseId, e);
            return List.of();
        }
    }

    private Optional<String> getMessageJson(String key) {

        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(key));
        } catch (Exception e) {
            log.warn("Failed to read key [{}] from Redis", key, e);
            return Optional.empty();
        }
    }

    private Optional<Message> parseMessage(String json) {

        try {
            return Optional.of(objectMapper.readValue(json, Message.class));
        } catch (Exception e) {
            log.warn("Failed to parse message JSON: {}", json, e);
            return Optional.empty();
        }
    }

    private String buildKey(UUID messageId) {

        return "message:" + messageId;
    }
}
