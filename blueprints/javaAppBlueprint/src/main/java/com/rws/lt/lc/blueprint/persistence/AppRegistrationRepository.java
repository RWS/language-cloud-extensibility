package com.rws.lt.lc.blueprint.persistence;

import com.rws.lt.lc.blueprint.domain.AppRegistration;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AppRegistrationRepository extends RetryableRepository<AppRegistration, String> {

    @Query("{}")
    Optional<AppRegistration> findFirst();

    @Query("{ 'accountId' : ?0, 'appId' : ?1 }")
    Optional<AppRegistration> findRegistration(String accountId, String appId);

    @Query(value = "{ 'accountId' : ?0, 'appId' : ?1 }", delete = true)
    void deleteRegistration(String accountId, String appId);

}
