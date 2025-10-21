package com.support_svc.service.impl;

import com.support_svc.model.Case;
import com.support_svc.model.enums.CaseStatus;
import com.support_svc.repository.CaseRepository;
import com.support_svc.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CaseCacheServiceImpl implements CacheService {

    private static final Logger log = LoggerFactory.getLogger(CaseCacheServiceImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final CaseRepository caseRepository;

    @Override
    public void saveCase(Case aCase) {

        String key = buildKey(aCase.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("id", aCase.getId().toString());
        map.put("caseOwner", aCase.getCaseOwner() != null ? aCase.getCaseOwner().toString() : null);
        map.put("requesterId", aCase.getRequesterId() != null ? aCase.getRequesterId().toString() : null);
        map.put("requesterName", aCase.getRequesterName());
        map.put("requesterEmail", aCase.getRequesterEmail());
        map.put("caseName", aCase.getCaseName());
        map.put("caseDescription", aCase.getCaseDescription());
        map.put("createdOn", aCase.getCreatedOn() != null ? aCase.getCreatedOn().toString() : null);
        map.put("updatedOn", aCase.getUpdatedOn() != null ? aCase.getUpdatedOn().toString() : null);
        map.put("status", aCase.getStatus() != null ? aCase.getStatus().name() : null);

        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.opsForSet().add("cases:all", aCase.getId().toString());

        log.info("Saving case with ID [{}] and status [{}]", aCase.getId(), aCase.getStatus());
    }

    @Override
    public Case getCase(UUID id) {

        String key = buildKey(id);
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);

        if (map.isEmpty()) {
            log.info("Cache miss for case ID [{}]", id);
            return null;
        } else {
            log.info("Cache hit for case ID [{}]", id);
        }

        return Case.builder()
                .id(UUID.fromString((String) map.get("id")))
                .caseOwner(map.get("caseOwner") != null ? UUID.fromString((String) map.get("caseOwner")) : null)
                .requesterId(map.get("requesterId") != null ? UUID.fromString((String) map.get("requesterId")) : null)
                .requesterName((String) map.get("requesterName"))
                .requesterEmail((String) map.get("requesterEmail"))
                .caseName((String) map.get("caseName"))
                .caseDescription((String) map.get("caseDescription"))
                .createdOn(map.get("createdOn") != null ? LocalDateTime.parse((String) map.get("createdOn")) : null)
                .updatedOn(map.get("updatedOn") != null ? LocalDateTime.parse((String) map.get("updatedOn")) : null)
                .status(map.get("status") != null ? CaseStatus.valueOf((String) map.get("status")) : null)
                .build();
    }

    @Override
    public void deleteCase(UUID id) {

        redisTemplate.delete(buildKey(id));
    }

    @Override
    public List<Case> findAllCached() {

        Set<Object> ids = redisTemplate.opsForSet().members("cases:all");

        if (ids == null || ids.isEmpty()) {
            List<Case> allCases = caseRepository.findAll();
            allCases.forEach(this::saveCase);
            log.info("Cache is empty. Loading {} cases from DB and populating cache", allCases.size());
            return allCases;
        }

        return ids.stream()
                .map(id -> this.getCase(UUID.fromString(id.toString())))
                .toList();
    }


    private String buildKey(UUID caseId) {

        return "case:" + caseId;
    }
}