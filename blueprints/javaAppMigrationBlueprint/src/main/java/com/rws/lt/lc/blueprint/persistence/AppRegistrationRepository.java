package com.rws.lt.lc.blueprint.persistence;

import com.rws.lt.lc.blueprint.domain.AppRegistration;
import org.springframework.data.mongodb.repository.Query;

public interface AppRegistrationRepository extends RetryableRepository<AppRegistration, String> {

    @Query("{}")
    AppRegistration findFirst();

    void deleteByAccountId(String accountId);

}
