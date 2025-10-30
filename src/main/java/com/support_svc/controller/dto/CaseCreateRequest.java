package com.support_svc.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CaseCreateRequest {

    private UUID requesterId;
    private String requesterName;
    private String requesterEmail;
    private String description;
}
