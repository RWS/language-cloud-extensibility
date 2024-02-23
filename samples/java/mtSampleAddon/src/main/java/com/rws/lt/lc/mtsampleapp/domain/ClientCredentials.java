package com.rws.lt.lc.mtsampleapp.domain;

import com.rws.lt.lc.mtsampleapp.transfer.lifecycle.ClientCredentialsTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString(exclude = "clientSecret")
@NoArgsConstructor
public class ClientCredentials {

    @Field("clientId")
    private String clientId;
    @Field("clientSecret")
    private String clientSecret;

    public ClientCredentials(ClientCredentialsTO clientCredentialsTO) {
        this.clientId = clientCredentialsTO.getClientId();
        this.clientSecret = clientCredentialsTO.getClientSecret();
    }

}
