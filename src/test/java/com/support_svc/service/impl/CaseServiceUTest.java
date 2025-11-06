package com.support_svc.service.impl;

import com.support_svc.controller.dto.CaseCreateRequest;
import com.support_svc.controller.dto.CaseResponse;
import com.support_svc.event.dto.CaseUpdateRequest;
import com.support_svc.exception.ResourceNotFoundException;
import com.support_svc.model.Case;
import com.support_svc.model.enums.CaseStatus;
import com.support_svc.repository.CaseRepository;
import com.support_svc.service.CacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceUTest {

    @Mock
    private CaseRepository caseRepository;
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private CaseServiceImpl caseService;


    @Test
    void whenCreateCase_thenHappyPath() {

        // given
        CaseCreateRequest request = CaseCreateRequest.builder()
                .requesterId(UUID.randomUUID())
                .requesterName("name")
                .requesterEmail("test@email.com")
                .description("description")
                .build();

        Case expectedCase = Case.builder()
                .id(UUID.randomUUID())
                .requesterId(request.getRequesterId())
                .caseDescription(request.getDescription())
                .caseName("case name")
                .caseOwnerEmail(request.getRequesterEmail())
                .requesterName(request.getRequesterName())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        // when
        when(caseRepository.save(any(Case.class))).thenReturn(expectedCase);

        Case createdCase = caseService.createCase(request);

        // then
        assertEquals(request.getDescription(), createdCase.getCaseDescription());
        assertNotNull(createdCase.getCreatedOn());

        verify(cacheService, times(1)).saveCase(createdCase);
        verify(caseRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Case.class));

    }

    @Test
    void whenGetCase_thenHappyPath() {

        // given
        Case expectedCase = Case.builder()
                .id(UUID.randomUUID())
                .requesterId(UUID.randomUUID())
                .caseDescription("description")
                .caseName("case name")
                .caseOwnerEmail("email@test.com")
                .requesterName("name")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        // when
        when(caseRepository.findById(expectedCase.getId())).thenReturn(Optional.of(expectedCase));
        Case aCase = caseService.getCase(expectedCase.getId());

        // then
        assertEquals(aCase.getCaseDescription(), expectedCase.getCaseDescription());
        assertNotNull(aCase);
        verify(cacheService, times(1)).saveCase(expectedCase);
        verify(cacheService, times(1)).getCase(expectedCase.getId());
        verify(caseRepository, times(1)).findById(any());
    }

    @Test
    void whenGetCase_thenThrowResourceNotFoundException() {

        UUID id = UUID.randomUUID();
        when(caseRepository.findById(id))
                .thenThrow(new ResourceNotFoundException("Case with id %s not found".formatted(id)));

        assertThrows(ResourceNotFoundException.class, () -> caseService.getCase(id));
    }

    @Test
    void whenGetAllCasesByOwnerEmail_thenHappyPath() {

        // given
        List<Case> cases = List.of(
                Case.builder().caseOwnerEmail("test@email.com").build(),
                Case.builder().caseOwnerEmail("test@email.com").build(),
                Case.builder().caseOwnerEmail("test@email.com").build());

        // when
        when(caseRepository.findAllByCaseOwnerEmail("test@email.com"))
                .thenReturn(cases);

        List<Case> allCasesByOwnerEmail = caseService.getAllCasesByOwnerEmail("test@email.com");

        // then
        assertEquals(allCasesByOwnerEmail.size(), 3);
    }

    @Test
    void whenUpdateCase_thenHappyPath() {

        // given
        CaseUpdateRequest request = CaseUpdateRequest.builder()
                .caseId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .ownerEmail("test@email.com")
                .caseName("case name")
                .description("this is a test description")
                .status(CaseStatus.NEW)
                .build();

        Case aCase = Case.builder()
                .id(request.getCaseId())
                .requesterId(request.getUserId())
                .caseDescription(request.getDescription())
                .caseName("case name")
                .caseOwnerEmail(request.getOwnerEmail())
                .requesterName("name")
                .status(request.getStatus())
                .updatedOn(LocalDateTime.now())
                .build();

        // when
        when(caseRepository.findById(request.getCaseId())).thenReturn(Optional.of(aCase));
        when(caseRepository.save(any())).thenReturn(aCase);
        Case updatedCase = caseService.updateCase(request);

        // then
        assertEquals(updatedCase.getCaseDescription(), request.getDescription());
        verify(cacheService, times(2)).saveCase(aCase);
    }

    @Test
    void whenFindAll_thenHappyPath() {

        // given
        List<Case> cases = List.of(
                Case.builder().caseOwnerEmail("test@email.com").build(),
                Case.builder().caseOwnerEmail("test2@email.com").build(),
                Case.builder().caseOwnerEmail("test3@email.com").build());
        // when
        when(caseRepository.findAll()).thenReturn(cases);
        List<CaseResponse> response = caseService.findAll();

        // then
        assertNotNull(response);
        assertEquals(response.size(), 3);
    }
}