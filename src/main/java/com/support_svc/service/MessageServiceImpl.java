package com.support_svc.service;

import com.support_svc.model.Message;
import com.support_svc.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message save(Message message) {

        return messageRepository.save(message);
    }

    @Override
    public Message getMessage(UUID id) {

        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    @Override
    public void deleteMessage(UUID id) {

        messageRepository.deleteById(id);
    }
}
