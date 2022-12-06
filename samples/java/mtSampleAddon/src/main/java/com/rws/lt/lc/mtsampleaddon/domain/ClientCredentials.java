package com.rws.lt.lc.mtsampleaddon.domain;

import com.rws.lt.lc.mtsampleaddon.transfer.lifecycle.ClientCredentialsTO;
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

    @Field("ci")
    private String clientId;
    @Field("cs")
    private String clientSecret;

    public ClientCredentials(ClientCredentialsTO clientCredentialsTO) {
        this.clientId = clientCredentialsTO.getClientId();
        this.clientSecret = clientCredentialsTO.getClientSecret();
    }

}
