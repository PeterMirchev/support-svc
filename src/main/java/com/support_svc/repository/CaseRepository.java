package com.support_svc.repository;

import com.support_svc.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping
public interface CaseRepository extends JpaRepository<Case, UUID> {

    List<Case> findAllByCaseOwnerIs(UUID userRequesterId);
}
