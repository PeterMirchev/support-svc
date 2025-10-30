package com.support_svc.model;

import com.support_svc.model.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String caseOwnerEmail;
    private UUID requesterId;
    private String requesterName;
    private String requesterEmail;
    private String caseName;
    private String caseDescription;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private CaseStatus status;
}

