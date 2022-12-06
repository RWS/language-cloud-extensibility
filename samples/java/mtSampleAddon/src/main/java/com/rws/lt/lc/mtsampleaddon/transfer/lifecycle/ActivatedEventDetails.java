package com.rws.lt.lc.mtsampleaddon.transfer.lifecycle;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class ActivatedEventDetails {
    @Valid
    private ClientCredentialsTO clientCredentials;
}
