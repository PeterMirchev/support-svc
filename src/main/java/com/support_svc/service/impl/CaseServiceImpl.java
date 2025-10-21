package com.support_svc.service.impl;

import com.support_svc.ResourceNotFoundException;
import com.support_svc.controller.dto.CaseResponse;
import com.support_svc.controller.dto.CreateCaseRequest;
import com.support_svc.controller.dto.UpdateCaseRequest;
import com.support_svc.model.Case;
import com.support_svc.model.Message;
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
    private final MessageServiceImpl messageService;
    private final CacheService cacheService;

    public CaseServiceImpl(CaseRepository caseRepository, MessageServiceImpl messageService, CacheService cacheService) {
        this.caseRepository = caseRepository;
        this.messageService = messageService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public Case createCase(CreateCaseRequest request) {

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
    public List<Case> getAllCasesByOwnerId(UUID userRequesterId) {

        return caseRepository.findAllByCaseOwnerIs(userRequesterId);
    }

    @Override
    public Case updateCase(UUID caseId, UpdateCaseRequest request, UUID userId) {

        Message message = Message.builder()
                .author(String.valueOf(userId))
                .text(request.getMessage())
                .dateTime(LocalDateTime.now())
                .build();

        Message persistedMessage = messageService.save(message);
        Case aCase = getCase(caseId);
        aCase.getMessages().add(persistedMessage);
        aCase.setCaseName(request.getCaseName() != null ? request.getCaseName() : aCase.getCaseName());
        aCase.setStatus(request.getCaseStatus() != null ? request.getCaseStatus() : aCase.getStatus());
        aCase.setUpdatedOn(LocalDateTime.now());

        Case updatedCase = caseRepository.save(aCase);
        cacheService.saveCase(updatedCase);

        log.info("Updating case [{}], new status [{}], adding message by user [{}]", caseId, request.getCaseStatus(), userId);

        return updatedCase;
    }

    @Override
    public void deleteCase() {

    }

    public List<CaseResponse> findAll() {

        List<Case> allCases = caseRepository.findAll();

        return allCases
                .stream()
                .map(Mapper::mapToCaseResponse)
                .toList();
    }

    public List<Case> findAllCached() {

        return cacheService.findAllCached();
    }

}
