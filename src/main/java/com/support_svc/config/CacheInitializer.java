package com.support_svc.config;

import com.support_svc.model.Case;
import com.support_svc.repository.CaseRepository;
import com.support_svc.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CacheInitializer.class);

    private final CaseRepository caseRepository;
    private final CacheService cacheService;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        try {
            List<Case> allCases = caseRepository.findAll();
            allCases.forEach(cacheService::saveCase);
            log.info("Redis cache successfully warmed up with {} cases", allCases.size());
        } catch (Exception e) {
            log.error("Failed to warm up Redis cache", e);
        }
    }
}