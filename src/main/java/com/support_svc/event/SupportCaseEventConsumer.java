package com.support_svc.event;

import com.support_svc.controller.dto.CreateCaseRequest;
import com.support_svc.controller.dto.SupportCaseEvent;
import com.support_svc.service.CaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SupportCaseEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(SupportCaseEventConsumer.class);

    private final CaseService caseService;

    public SupportCaseEventConsumer(CaseService caseService) {
        this.caseService = caseService;
    }

    @KafkaListener(topics = "case-events", groupId = "support-service-group")
    public void handleSupportCase(SupportCaseEvent event) {

        if (event == null) {
            log.warn("Received null event, skipping");
            return;
        }

        try {
            log.info("📥 Received support case event: {}", event);

            // Map to internal DTO
            CreateCaseRequest request = CreateCaseRequest.builder()
                    .requesterId(event.getRequesterId())
                    .requesterName(event.getRequesterName())
                    .requesterEmail(event.getRequesterEmail())
                    .description(event.getDescription())
                    .build();

            // Create the case
            caseService.createCase(request);

            log.info("Support case created successfully for requesterId: {}", event.getRequesterId());

        } catch (Exception e) {
            // Handle any processing errors
            log.error("Failed to process support case event: {}", event, e);
        }
    }
}
