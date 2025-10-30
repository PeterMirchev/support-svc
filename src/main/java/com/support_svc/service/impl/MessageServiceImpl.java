package com.support_svc.service.impl;

import com.support_svc.event.dto.CaseMessageRequest;
import com.support_svc.model.Message;
import com.support_svc.repository.MessageRepository;
import com.support_svc.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageCacheServiceImpl messageCacheService;

    public MessageServiceImpl(MessageRepository messageRepository, MessageCacheServiceImpl messageCacheService) {
        this.messageRepository = messageRepository;
        this.messageCacheService = messageCacheService;
    }

    @Override
    @Transactional
    public void sendMessage(CaseMessageRequest event) {

        Message message = Message.builder()
                .caseId(event.getCaseId())
                .author(event.getAuthor())
                .dateTime(event.getDateTime())
                .text(event.getText())
                .build();

        Message persisted = messageRepository.save(message);

        messageCacheService.saveMessage(persisted);

        log.info("Message [{}] sent and cached for case [{}]", persisted.getId(), persisted.getCaseId());
    }

    @Override
    public Message getMessage(UUID id) {

        Message cachedMessage = messageCacheService.getMessage(id);
        if (cachedMessage != null) {
            log.info("Cache hit for message [{}]", id);
            return cachedMessage;
        }

        log.info("Cache miss for message [{}], fetching from DB", id);
        Message dbMessage = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        messageCacheService.saveMessage(dbMessage);
        log.info("Message [{}] saved to Redis after DB fetch", id);

        return dbMessage;
    }

    @Override
    public List<Message> getAllMessagesByCase(UUID caseId) {

        List<Message> cachedMessages = messageCacheService.getAllMessagesByCase(caseId);
        if (cachedMessages != null && !cachedMessages.isEmpty()) {
            log.info("Cache hit for messages of case [{}]", caseId);
            return cachedMessages;
        }

        log.info("Cache miss for messages of case [{}], fetching from DB", caseId);
        List<Message> dbMessages = messageRepository.findAllByCaseIdOrderByDateTimeAsc(caseId);

        dbMessages.forEach(messageCacheService::saveMessage);
        log.info("Cached {} messages for case [{}]", dbMessages.size(), caseId);

        return dbMessages;
    }

    @Override
    public void deleteMessage(UUID id) {

        messageRepository.deleteById(id);
        log.info("Message [{}] deleted from DB", id);
    }
}
