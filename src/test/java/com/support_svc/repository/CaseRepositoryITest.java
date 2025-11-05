package com.support_svc.repository;

import com.support_svc.TestRedisConfig;
import com.support_svc.model.Case;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class CaseRepositoryITest {

    @Autowired
    private CaseRepository caseRepository;

    @Test
    void whenSave_thenFindCaseHappyPath() {

        // given
        Case entity = Case.builder()
                .requesterName("Random name")
                .requesterId(UUID.randomUUID())
                .caseOwnerEmail("random@email.com")
                .caseDescription("test description")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        // when
        Case saved = caseRepository.saveAndFlush(entity);

        // then
        var result = caseRepository.findById(saved.getId());
        assertTrue(result.isPresent());
        assertEquals("random@email.com", result.get().getCaseOwnerEmail());
    }
}
