package com.support_svc.controller.dto;

import com.support_svc.model.Message;
import com.support_svc.model.enums.CaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CaseResponse {

    private UUID id;
    private String caseOwnerEmail;
    private UUID requesterId;
    private String requesterName;
    private String requesterEmail;
    private String caseName;
    private String caseDescription;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private CaseStatus status;
}
