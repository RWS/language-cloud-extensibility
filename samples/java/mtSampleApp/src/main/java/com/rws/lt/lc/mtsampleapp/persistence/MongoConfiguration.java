package com.rws.lt.lc.mtsampleapp.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;

@EnableMongoRepositories(repositoryBaseClass = RetryableRepositoryImpl.class, basePackages = {"com.rws.lt.lc.mtsampleapp.persistence"})
@EnableMongoAuditing
@Configuration
@Slf4j
public class MongoConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        LOGGER.info("Starting creating MongoDb indices");
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();

        Collection<? extends MongoPersistentEntity<?>> entities = mappingContext.getPersistentEntities();
        for (MongoPersistentEntity<?> entity : entities) {
            Class<?> entityType = entity.getType();
            if (!entityType.isAnnotationPresent(Document.class)) {
                continue;
            }

            IndexOperations indexOps = mongoTemplate.indexOps(entityType);
            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
            try {
                resolver.resolveIndexFor(entityType).forEach(indexOps::ensureIndex);
            } catch (Exception e) {
                LOGGER.warn("Failed to create Mongo index for type={}. Continuing application startup.", entityType.getSimpleName(), e);
            }
        }
    }
}