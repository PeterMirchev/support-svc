package com.support_svc.service.impl;

import com.support_svc.exception.ResourceNotFoundException;
import com.support_svc.controller.dto.CaseResponse;
import com.support_svc.event.dto.CaseUpdateRequest;
import com.support_svc.controller.dto.CaseCreateRequest;
import com.support_svc.model.Case;
import com.support_svc.repository.CaseRepository;
import com.support_svc.service.CacheService;
import com.support_svc.service.CaseService;
import com.support_svc.utils.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CaseServiceImpl implements CaseService {

    private static final Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);

    private final CaseRepository caseRepository;

    private final CacheService cacheService;

    public CaseServiceImpl(CaseRepository caseRepository, CacheService cacheService) {
        this.caseRepository = caseRepository;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public Case createCase(CaseCreateRequest request) {

        Case newCase = Mapper.mapToCase(request);

        Case persistedCase =  caseRepository.save(newCase);
        cacheService.saveCase(persistedCase);

        log.info("Case with id [{}] created", persistedCase.getId());

        return persistedCase;
    }

    @Override
    public Case getCase(UUID id) {

        Case aCase = cacheService.getCase(id);
        if (aCase != null) {
            log.info("Cache hit for case ID [{}]", id);
            return aCase;
        }

        log.info("Cache miss for case ID [{}], loading from DB", id);
        aCase = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case with id " + id + " not found"));

        cacheService.saveCase(aCase);
        log.info("Case ID [{}] saved to cache after DB fetch", id);
        return aCase;
    }


    @Override
    public List<Case> getAllCasesByOwnerEmail(String ownerEmail) {

        return caseRepository.findAllByCaseOwnerEmail(ownerEmail);
    }

    @Override
    public Case updateCase(CaseUpdateRequest caseUpdateRequest) {

        Case aCase = mapCase(caseUpdateRequest);

        Case persistedCase = caseRepository.save(aCase);
        cacheService.saveCase(persistedCase);

        log.info("User with email: [{}] updated case [{}]", caseUpdateRequest.getOwnerEmail(), aCase.getId());

        return persistedCase;
    }

    @Override
    public void deleteCase() {

    }

    @Override
    public List<CaseResponse> findAll() {

        List<Case> allCases = caseRepository.findAll();

        return allCases
                .stream()
                .map(Mapper::mapToCaseResponse)
                .toList();
    }

    private Case mapCase(CaseUpdateRequest caseUpdateRequest) {

        Case aCase = getCase(caseUpdateRequest.getCaseId());

        if (caseUpdateRequest.getCaseName() != null) {
            aCase.setCaseName(caseUpdateRequest.getCaseName());
        }
        if (caseUpdateRequest.getDescription() != null) {
            aCase.setCaseDescription(caseUpdateRequest.getDescription());
        }
        if (caseUpdateRequest.getStatus() != aCase.getStatus()) {

            aCase.setStatus(caseUpdateRequest.getStatus());
        }

        aCase.setCaseOwnerEmail(caseUpdateRequest.getOwnerEmail());
        aCase.setUpdatedOn(LocalDateTime.now());
        return aCase;
    }
}
