package com.rws.lt.lc.mtsampleapp.persistence;

import com.rws.lt.lc.mtsampleapp.domain.AppRegistration;

public interface AppRegistrationRepository extends RetryableRepository<AppRegistration, String> {

    AppRegistration findByAccountId(String accountId);

    void deleteByAccountId(String accountId);

}
