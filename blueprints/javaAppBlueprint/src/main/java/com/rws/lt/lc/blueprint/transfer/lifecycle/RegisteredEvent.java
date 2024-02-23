package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonTypeName(AppLifecycleEvent.REGISTERED)
public class RegisteredEvent extends AppLifecycleEvent {

    public RegisteredEvent() {
        super(REGISTERED);
    }

    @Valid
    @NotNull
    private RegisteredEventDetails data;
}