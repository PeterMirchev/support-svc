package com.support_svc.controller;

import com.support_svc.controller.dto.CaseResponse;
import com.support_svc.controller.dto.CreateCaseRequest;
import com.support_svc.model.Case;
import com.support_svc.service.CaseService;
import com.support_svc.utils.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping()
    public ResponseEntity<CaseResponse> create(@RequestBody CreateCaseRequest request) {

        Case newCase = caseService.createCase(request);

        CaseResponse caseResponse = Mapper.mapToCaseResponse(newCase);

        return ResponseEntity.ok(caseResponse);
    }
}
