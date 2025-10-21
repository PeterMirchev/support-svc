package com.support_svc.service;

import com.support_svc.model.Case;

import java.util.UUID;

public interface CacheService {

    void saveCase(Case c);

    Case getCase(UUID id);

    void deleteCase(UUID id);
}
