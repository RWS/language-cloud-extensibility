package com.rws.lt.lc.blueprint.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Document(collection = "accountSettings")
public class AccountSettings extends PersistedDomain {

    @Indexed(background = true, unique = true)
    @Field("aid")
    private String accountId;

    @Field("configs")
    private Map<String, String> configurations = new HashMap<>();

    @Field("ccreds")
    private ClientCredentials clientCredentials;
}
