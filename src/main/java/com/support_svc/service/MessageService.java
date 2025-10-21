package com.support_svc.service;

import com.support_svc.model.Message;

import java.util.UUID;

public interface MessageService {

    Message save(Message message);

    Message getMessage(UUID id);

    void deleteMessage(UUID id);

}
