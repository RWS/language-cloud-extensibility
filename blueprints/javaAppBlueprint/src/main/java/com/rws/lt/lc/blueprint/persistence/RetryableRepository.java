package com.rws.lt.lc.blueprint.persistence;

import com.rws.lt.lc.blueprint.exception.NotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NoRepositoryBean
public interface RetryableRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

    int MAX_RETRY_ATTEMPTS = 5;

    T update(ID id, Consumer<T> updater) throws NotFoundException;

    T save(Supplier<T> query, Function<T, T> updater);
}

