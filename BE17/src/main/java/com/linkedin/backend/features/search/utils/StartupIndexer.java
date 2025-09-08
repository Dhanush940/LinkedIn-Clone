package com.linkedin.backend.features.search.utils;

import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StartupIndexer {
    private final EntityManager entityManager;

    public StartupIndexer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional  // 🔑 ensures we have a transactional EntityManager
    public void reindex() {
        try {
            Search.session(entityManager)
                 .massIndexer()
                 .startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
