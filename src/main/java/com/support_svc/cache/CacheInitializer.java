package com.support_svc.cache;

import com.support_svc.model.Case;
import com.support_svc.model.Message;
import com.support_svc.repository.CaseRepository;
import com.support_svc.repository.MessageRepository;
import com.support_svc.service.CacheService;
import com.support_svc.service.impl.MessageCacheServiceImpl;
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
    private final MessageRepository messageRepository;
    private final CacheService cacheService;
    private final MessageCacheServiceImpl messageCacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        try {
            List<Case> allCases = caseRepository.findAll();
            allCases.forEach(cacheService::saveCase);
            log.info("Redis cache warmed up with {} cases", allCases.size());
        } catch (Exception e) {
            log.error("Failed to warm up Redis cache for cases", e);
        }

        try {
            List<Message> allMessages = messageRepository.findAll();
            allMessages.forEach(messageCacheService::saveMessage);
            log.info("Redis cache warmed up with {} messages", allMessages.size());
        } catch (Exception e) {
            log.error("Failed to warm up Redis cache for messages", e);
        }
    }
}