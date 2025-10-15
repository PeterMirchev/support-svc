package com.support_svc.utils;

import com.support_svc.controller.dto.CaseResponse;
import com.support_svc.controller.dto.CreateCaseRequest;
import com.support_svc.model.Case;
import com.support_svc.model.enums.CaseStatus;

import java.time.LocalDateTime;

public class Mapper {

    public static Case mapToCase(CreateCaseRequest request) {

        return Case.builder()
                .requesterId(request.getRequesterId())
                .requesterName(request.getRequesterName())
                .requesterEmail(request.getRequesterEmail())
                .caseDescription(request.getDescription())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .status(CaseStatus.NEW)
                .build();
    }

    public static CaseResponse mapToCaseResponse(Case aCase) {

        return CaseResponse.builder()
                .id(aCase.getId())
                .caseOwner(aCase.getCaseOwner())
                .requesterId(aCase.getRequesterId())
                .requesterName(aCase.getRequesterName())
                .requesterEmail(aCase.getRequesterEmail())
                .caseName(aCase.getCaseName())
                .caseDescription(aCase.getCaseDescription())
                .createdOn(aCase.getCreatedOn())
                .updatedOn(aCase.getUpdatedOn())
                .status(aCase.getStatus())
                .messages(aCase.getMessages())
                .build();
    }
}
