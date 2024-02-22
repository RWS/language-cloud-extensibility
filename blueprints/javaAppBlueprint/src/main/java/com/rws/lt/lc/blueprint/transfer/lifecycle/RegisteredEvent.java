package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AppLifecycleEvent.REGISTERED)
public class RegisteredEvent extends AppLifecycleEvent {
    public RegisteredEvent() {
        super(REGISTERED);
    }
}
