package com.support_svc.service;

import com.support_svc.ResourceNotFoundException;
import com.support_svc.controller.dto.CreateCaseRequest;
import com.support_svc.controller.dto.UpdateCaseRequest;
import com.support_svc.model.Case;
import com.support_svc.model.enums.CaseStatus;
import com.support_svc.repository.CaseRepository;
import com.support_svc.utils.Mapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CaseService implements CaseI{

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Override
    public Case createCase(CreateCaseRequest request) {

        Case newCase = Mapper.mapToCase(request);

        return caseRepository.save(newCase);
    }

    @Override
    public Case getCase(UUID id) {

        return caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case with id " + id + " not found"));
    }

    @Override
    public List<Case> getAllCasesByOwnerId(UUID userRequesterId) {

        return caseRepository.findAllByCaseOwnerIs(userRequesterId);
    }

    @Override
    public Case updateCase(UpdateCaseRequest request) {
        return null;
    }

    @Override
    public void deleteCase() {

    }
}
