package com.support_svc.event.dto;

import com.support_svc.model.enums.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseUpdateRequest {

    private UUID caseId;
    private UUID userId;
    private String ownerEmail;
    private String caseName;
    private String description;
    private CaseStatus status;
}
