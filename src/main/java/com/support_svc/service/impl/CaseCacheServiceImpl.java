package com.support_svc.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.support_svc.model.Case;
import com.support_svc.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseCacheServiceImpl implements CacheService {

    private static final Logger log = LoggerFactory.getLogger(CaseCacheServiceImpl.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveCase(Case aCase) {

        try {
            String key = buildKey(aCase.getId());
            String json = objectMapper.writeValueAsString(aCase);

            redisTemplate.opsForValue().set(key, json);
            redisTemplate.opsForSet().add("cases:all", key);

            log.info("Saved case [{}] to Redis as JSON", aCase.getId());
        } catch (Exception e) {
            log.error("Failed to save case [{}] to Redis", aCase.getId(), e);
        }
    }

    @Override
    public Case getCase(UUID id) {

        try {
            String key = buildKey(id);
            String json = redisTemplate.opsForValue().get(key);

            if (json == null) {
                log.info("Cache miss for case [{}]", id);
                return null;
            }

            log.info("Cache hit for case [{}]", id);

            JsonNode node = objectMapper.readTree(json);

            return objectMapper.treeToValue(node, Case.class);
        } catch (Exception e) {
            log.error("Failed to read case [{}] from Redis", id, e);
            return null;
        }
    }

    private String buildKey(UUID caseId) {

        return "case:" + caseId;
    }
}
