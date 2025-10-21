package com.support_svc.controller;

import com.support_svc.controller.dto.CaseResponse;
import com.support_svc.controller.dto.CreateCaseRequest;
import com.support_svc.controller.dto.UpdateCaseRequest;
import com.support_svc.model.Case;
import com.support_svc.service.CaseServiceImpl;
import com.support_svc.utils.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseServiceImpl caseServiceImpl;

    public CaseController(CaseServiceImpl caseServiceImpl) {
        this.caseServiceImpl = caseServiceImpl;
    }

    @PostMapping()
    public ResponseEntity<CaseResponse> create(@RequestBody CreateCaseRequest request) {

        Case newCase = caseServiceImpl.createCase(request);

        CaseResponse caseResponse = Mapper.mapToCaseResponse(newCase);

        return ResponseEntity.ok(caseResponse);
    }

    @PutMapping("/{caseId}")
    public ResponseEntity<CaseResponse> update(@RequestBody UpdateCaseRequest request,
                                               @PathVariable UUID caseId,
                                               @RequestHeader UUID userId) {

        Case aCase = caseServiceImpl.updateCase(caseId, request, userId);

        CaseResponse caseResponse = Mapper.mapToCaseResponse(aCase);

        return ResponseEntity.ok(caseResponse);
    }

    @GetMapping
    public ResponseEntity<List<CaseResponse>> getAll() {

        List<CaseResponse> responses = caseServiceImpl.findAll();

        return ResponseEntity.ok(responses);
    }
}
