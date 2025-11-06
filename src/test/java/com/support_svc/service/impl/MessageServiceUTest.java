package com.support_svc.service.impl;

import com.support_svc.event.dto.CaseMessageRequest;
import com.support_svc.model.Message;
import com.support_svc.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceUTest {

    @Mock
    private MessageCacheServiceImpl messageCacheService;
    @Mock
    private MessageRepository messageRepository;
    @InjectMocks
    MessageServiceImpl messageService;

    @Test
    void whenSendMessage_thenHappyPath() {

        // given
        CaseMessageRequest request = CaseMessageRequest.builder()
                .author("author")
                .caseId(UUID.randomUUID())
                .text("text")
                .dateTime(LocalDateTime.now())
                .build();

        Message message = Message.builder()
                .caseId(request.getCaseId())
                .text(request.getText())
                .author(request.getAuthor())
                .dateTime(request.getDateTime())
                .build();
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // when
        messageService.sendMessage(request);

        // then
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void whenGetMessage_thenHappyPath() {

        // given
        UUID id = UUID.randomUUID();

        Message message = Message.builder()
                .caseId(UUID.randomUUID())
                .id(id)
                .text("text")
                .dateTime(LocalDateTime.now())
                .author("author")
                .build();

        when(messageCacheService.getMessage(id))
                .thenReturn(null);
        when(messageRepository.findById(id))
                .thenReturn(Optional.ofNullable(message));

        // when
        Message dbMessage = messageService.getMessage(id);

        // then
        verify(messageCacheService, times(1)).getMessage(id);
        verify(messageRepository, times(1)).findById(id);

        assertEquals(dbMessage.getId(), id);
        assertNotNull(dbMessage);
    }

    @Test
    void whenGetAllMessagesByCase_thenHappyPath() {

        // given
        UUID caseId = UUID.randomUUID();
        List<Message> messages = List.of(
                Message.builder().caseId(caseId).build(),
                Message.builder().caseId(caseId).build(),
                Message.builder().caseId(caseId).build()
        );

        when(messageCacheService.getAllMessagesByCase(caseId)).thenReturn(null);
        when(messageRepository.findAllByCaseIdOrderByDateTimeAsc(caseId)).thenReturn(messages);

        // when
        List<Message> allMessagesByCase = messageService.getAllMessagesByCase(caseId);

        // then
        assertNotNull(allMessagesByCase);
        assertEquals(allMessagesByCase.size(), messages.size());

        verify(messageCacheService, times(1)).getAllMessagesByCase(caseId);
    }
}
