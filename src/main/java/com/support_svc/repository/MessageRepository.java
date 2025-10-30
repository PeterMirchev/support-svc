package com.support_svc.repository;

import com.support_svc.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByCaseIdOrderByDateTimeAsc(UUID caseId);
}
