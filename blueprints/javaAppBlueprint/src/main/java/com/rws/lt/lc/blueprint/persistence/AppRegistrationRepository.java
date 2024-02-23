package com.rws.lt.lc.blueprint.persistence;

import com.rws.lt.lc.blueprint.domain.AppRegistration;

public interface AppRegistrationRepository extends RetryableRepository<AppRegistration, String> {

    AppRegistration findByAccountId(String accountId);

    void deleteByAccountId(String accountId);

}