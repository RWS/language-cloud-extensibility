package com.rws.lt.lc.blueprint.persistence;

import com.google.common.collect.Sets;
import com.rws.lt.lc.blueprint.persistence.retry.RetryTemplateBuilder;
import com.rws.lt.lc.blueprint.exception.NotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.retry.support.RetryTemplate;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NoRepositoryBean
public class RetryableRepositoryImpl<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements RetryableRepository<T, ID> {

    private final RetryTemplate updateTemplate;
    private final RetryTemplate saveTemplate;

    public RetryableRepositoryImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        Set<Class<? extends Throwable>> es = Sets.newHashSet(
                OptimisticLockingFailureException.class,
                org.springframework.dao.DuplicateKeyException.class,
                com.mongodb.DuplicateKeyException.class
        );
        this.saveTemplate = RetryTemplateBuilder.build(MAX_RETRY_ATTEMPTS, es);
        this.updateTemplate = RetryTemplateBuilder.build(MAX_RETRY_ATTEMPTS, Sets.newHashSet(OptimisticLockingFailureException.class));
    }

    @Override
    public T update(ID id, Consumer<T> updater) throws NotFoundException {
        return updateTemplate.execute(c -> {
            Optional<T> optionalEntity = findById(id);

            T entity = optionalEntity.orElseThrow(() -> new NotFoundException("No entity found with id: " + id.toString()));

            updater.accept(entity);
            save(entity);
            return entity;
        });
    }

    /**
     * Save with retries.
     * Conditions:
     * - if the entity doesn't exist it will be created
     * - if the creation throws DuplicateKeyException then in the next retry it will update the entity
     * - if the entity exist it will be updated
     * - if the update throws OptimisticLockingFailureException then it will try again
     */
    @Override
    public T save(Supplier<T> query, Function<T, T> updater) {
        return saveTemplate.execute(c -> {
            T entity = updater.apply(query.get());
            save(entity);
            return entity;
        });
    }
}
