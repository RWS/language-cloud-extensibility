package com.rws.lt.lc.blueprint.persistence;

import com.rws.lt.lc.blueprint.domain.AccountSettings;
import org.springframework.data.mongodb.repository.Query;

public interface AccountSettingsRepository extends RetryableRepository<AccountSettings, String>, AccountSettingsAtomicRepository {

    @Query("{ 'accountId' : ?0}")
    AccountSettings findAccountSettings(String accountId);

}
