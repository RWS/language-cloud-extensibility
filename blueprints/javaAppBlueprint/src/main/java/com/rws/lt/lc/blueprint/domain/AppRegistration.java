package com.rws.lt.lc.blueprint.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "appRegistration")
public class AppRegistration extends PersistedDomain {

    @Field("accountId")
    private String accountId;

    @Field("appId")
    private String appId;

    @Field("clientCredentials")
    private ClientCredentials clientCredentials;
}