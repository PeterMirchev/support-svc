package com.support_svc.service;

import com.support_svc.controller.dto.CaseResponse;
import com.support_svc.event.dto.CaseMessageRequest;
import com.support_svc.event.dto.CaseUpdateRequest;
import com.support_svc.controller.dto.CaseCreateRequest;
import com.support_svc.model.Case;

import java.util.List;
import java.util.UUID;

public interface CaseService {

    Case createCase(CaseCreateRequest request);

    Case getCase(UUID id);

    List<Case> getAllCasesByOwnerEmail(String ownerEmail);

    Case updateCase(CaseUpdateRequest caseUpdateRequest);

    List<CaseResponse> findAll();
}
