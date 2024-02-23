package com.rws.lt.lc.blueprint.transfer.lifecycle;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class RegisteredEventDetails {

    @Valid
    private ClientCredentialsTO clientCredentials;
}
