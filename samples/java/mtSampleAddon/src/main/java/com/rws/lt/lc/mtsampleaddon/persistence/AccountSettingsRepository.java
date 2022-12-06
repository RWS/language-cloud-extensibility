package com.rws.lt.lc.mtsampleaddon.persistence;

import com.rws.lt.lc.mtsampleaddon.domain.AccountSettings;
import org.springframework.data.mongodb.repository.Query;

public interface AccountSettingsRepository extends RetryableRepository<AccountSettings, String> {

    @Query("{ 'aid' : ?0}")
    AccountSettings findAccountSettings(String accountId);

}
