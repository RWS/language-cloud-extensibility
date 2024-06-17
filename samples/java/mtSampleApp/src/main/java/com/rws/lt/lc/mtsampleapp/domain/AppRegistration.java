package com.rws.lt.lc.mtsampleapp.domain;

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

    @Field("clientCredentials")
    private ClientCredentials clientCredentials;
}