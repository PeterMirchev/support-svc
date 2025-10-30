package com.support_svc.service;

import com.support_svc.event.dto.CaseMessageRequest;
import com.support_svc.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {



    void sendMessage(CaseMessageRequest event);

    Message getMessage(UUID id);

    List<Message> getAllMessagesByCase(UUID caseId);

    void deleteMessage(UUID id);

}
