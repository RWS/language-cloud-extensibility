package com.rws.lt.lc.mtsampleapp.persistence;

import com.rws.lt.lc.mtsampleapp.domain.AccountSettings;
import org.springframework.data.mongodb.repository.Query;

public interface AccountSettingsRepository extends RetryableRepository<AccountSettings, String> {

    @Query("{ 'accountId' : ?0}")
    AccountSettings findAccountSettings(String accountId);

}
