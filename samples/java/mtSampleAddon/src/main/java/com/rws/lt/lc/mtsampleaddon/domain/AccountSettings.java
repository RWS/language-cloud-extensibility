package com.rws.lt.lc.mtsampleaddon.domain;

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

    public static final String SAMPLE_ACCOUNT_SECRET = "SAMPLE_ACCOUNT_SECRET";
    public static final String SAMPLE_PROJECT_ID = "SAMPLE_PROJECT_ID";
    public static final String SAMPLE_LOCATION = "SAMPLE_LOCATION";

    private static final String DEFAULT_LOCATION = "global";

    @Indexed(background = true, unique = true)
    @Field("aid")
    private String accountId;

    @Field("configs")
    private Map<String, String> configurations = new HashMap<>();

    @Field("ccreds")
    private ClientCredentials clientCredentials;

    public String getGoogleProjectId() {
        return getConfigurations().get(SAMPLE_PROJECT_ID);
    }

    public String getLocation() {
        String location = getConfigurations().get(SAMPLE_LOCATION);
        return location == null ? DEFAULT_LOCATION : location;
    }

    public String getPlainLocation() {
        return getConfigurations().get(SAMPLE_LOCATION);
    }

    public String getServiceAccountKey() {
        return getConfigurations().get(SAMPLE_ACCOUNT_SECRET);
    }

}
