package com.rws.lt.lc.blueprint.persistence;

import com.rws.lt.lc.blueprint.domain.AccountSettings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AccountSettingsAtomicRepositoryImpl implements AccountSettingsAtomicRepository {

    private MongoTemplate mongoTemplate;

    public AccountSettingsAtomicRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void removeAccountsClientCredentials() {
        Query query = new Query();
        Update update = new Update().unset("ccreds").unset("clientCredentials");
        mongoTemplate.updateMulti(query, update, AccountSettings.class);
    }
}
