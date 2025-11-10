package com.support_svc.service.impl;

import com.support_svc.config.TestRedisConfig;
import com.support_svc.event.dto.CaseMessageRequest;
import com.support_svc.model.Message;
import com.support_svc.repository.CaseRepository;
import com.support_svc.repository.MessageRepository;
import com.support_svc.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class MessageServiceITest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private MessageCacheServiceImpl messageCacheService;
    @Autowired
    private MessageService messageService;

    @Test
    void whenSendMessage_thenHappyPath() {

        // given
        UUID caseId = UUID.randomUUID();

        CaseMessageRequest request = CaseMessageRequest.builder()
                .author("author")
                .caseId(caseId)
                .dateTime(LocalDateTime.now())
                .text("test message")
                .build();

        messageService.sendMessage(request);

        List<Message> dbMessages = messageRepository.findAllByCaseIdOrderByDateTimeAsc(caseId);
        assertFalse(dbMessages.isEmpty(), "Message should be persisted in DB");
        Message persistedMessage = dbMessages.get(0);
        assertEquals("test message", persistedMessage.getText());

        List<Message> cachedMessages = messageService.getAllMessagesByCase(caseId);
        assertFalse(cachedMessages.isEmpty(), "Cache should contain message after sending");
        assertEquals("test message", cachedMessages.get(0).getText());

        Message fetchedFromCache = messageService.getMessage(persistedMessage.getId());
        assertNotNull(fetchedFromCache, "Fetched message should not be null");
        assertEquals(persistedMessage.getId(), fetchedFromCache.getId());

        messageCacheService.evictAll();
        Message fetchedFromDb = messageService.getMessage(persistedMessage.getId());
        assertNotNull(fetchedFromDb, "Should retrieve message from DB after cache miss");
        assertEquals(persistedMessage.getText(), fetchedFromDb.getText());
    }

}
