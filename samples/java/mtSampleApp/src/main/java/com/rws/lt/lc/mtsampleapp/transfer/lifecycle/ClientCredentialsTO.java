package com.rws.lt.lc.mtsampleapp.transfer.lifecycle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString(exclude = "clientSecret")
@NoArgsConstructor
public class ClientCredentialsTO {

    @Valid
    @NotNull
    @NotBlank
    private String clientId;

    @Valid
    @NotNull
    @NotBlank
    private String clientSecret;

    public ClientCredentialsTO(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
