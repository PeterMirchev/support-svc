package com.support_svc.controller.dto;

import com.support_svc.model.enums.CaseStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCaseRequest {

    private String caseName;
    private CaseStatus caseStatus;
    private String message;
}
