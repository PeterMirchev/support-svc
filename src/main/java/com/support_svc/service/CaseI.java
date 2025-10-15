package com.support_svc.service;

import com.support_svc.controller.dto.CreateCaseRequest;
import com.support_svc.controller.dto.UpdateCaseRequest;
import com.support_svc.model.Case;

import java.util.List;
import java.util.UUID;

public interface CaseI {

    Case createCase(CreateCaseRequest request);

    Case getCase(UUID id);

    List<Case> getAllCasesByOwnerId(UUID userRequesterId);

    Case updateCase(UpdateCaseRequest request);

    void deleteCase();
}
