package com.support_svc.event;

import com.support_svc.controller.dto.CaseCreateRequest;
import com.support_svc.event.dto.CaseMessageRequest;
import com.support_svc.event.dto.CaseUpdateRequest;
import com.support_svc.event.dto.SupportCaseEvent;
import com.support_svc.service.CaseService;
import com.support_svc.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SupportCaseEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(SupportCaseEventConsumer.class);

    private final CaseService caseService;
    private final MessageService messageService;

    public SupportCaseEventConsumer(CaseService caseService, MessageService messageService) {
        this.caseService = caseService;
        this.messageService = messageService;
    }

    @KafkaListener(topics = "${kafka.topic.case-create}",  containerFactory = "supportCaseKafkaListenerFactory")
    public void handleCreate(SupportCaseEvent event) {

        try {
            CaseCreateRequest request = CaseCreateRequest.builder()
                    .requesterId(event.getRequesterId())
                    .requesterEmail(event.getRequesterEmail())
                    .requesterName(event.getRequesterName())
                    .description(event.getDescription())
                    .build();
            caseService.createCase(request);
            log.info("Received create event: {}", event);
        } catch (Exception e) {
            log.error("Error handling create event: {}", event, e);
            throw e;
        }
    }


    @KafkaListener(topics = "${kafka.topic.case-update}", containerFactory = "caseUpdateKafkaListenerFactory")
    public void handleUpdate(CaseUpdateRequest event) {

        try {
            log.info("Processing update event: {}", event);
            caseService.updateCase(event);
            log.info("Received update event: {}", event);
        } catch (Exception e) {
            log.error("Error handling update event: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${kafka.topic.case-message}", containerFactory = "caseMessageKafkaListenerFactory")
    public void handleMessage(CaseMessageRequest event) {

        try {
            log.info("Processing update event: {}", event);
            messageService.sendMessage(event);
            log.info("Received message event: {}", event);
        } catch (Exception e) {
            log.error("Error handling update event: {}", event, e);
            throw e;
        }
    }
}