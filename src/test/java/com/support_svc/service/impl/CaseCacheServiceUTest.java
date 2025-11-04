package com.support_svc.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.support_svc.model.Case;
import com.support_svc.service.impl.CaseCacheServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.SetOperations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaseCacheServiceUTest {

    @InjectMocks
    private CaseCacheServiceImpl cacheService;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOps;
    @Mock
    private SetOperations<String, String> setOps;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    void whenSaveCase_thenHappyPath() throws JsonProcessingException {

        // given
        Case aCase = Case.builder()
                .id(UUID.randomUUID())
                .caseName("Test Case")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // when
        cacheService.saveCase(aCase);

        // then
        String key = "case:" + aCase.getId();
        verify(valueOps).set(eq(key), anyString());
        verify(setOps).add("cases:all", key);
    }

    @Test
    void whenSaveCase_thenThrowException() {

        // given
        Case aCase = Case.builder().id(UUID.randomUUID()).build();

        // when
        when(redisTemplate.opsForValue())
                .thenThrow(new RuntimeException("Redis unavailable"));
        cacheService.saveCase(aCase);

        // then
        verify(redisTemplate, times(1)).opsForValue();
    }

    @Test
    void whenGetCase_thenHappyPath() {

        // given
        UUID id = UUID.randomUUID();
        String key = "case:" + id;

        // when
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn("{}");

        Case result = cacheService.getCase(id);

        // then
        assertNull(result);
        verify(redisTemplate.opsForValue()).get(key);
    }

    @Test
    void whenGetCase_thenThrowException() {

        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis fail"));

        cacheService.getCase(UUID.randomUUID());

        verify(redisTemplate, times(1)).opsForValue();
    }
}
