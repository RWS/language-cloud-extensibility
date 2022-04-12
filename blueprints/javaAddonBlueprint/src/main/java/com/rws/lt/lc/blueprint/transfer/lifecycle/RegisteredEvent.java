package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AddonLifecycleEvent.REGISTERED)
public class RegisteredEvent extends AddonLifecycleEvent {
    public RegisteredEvent() {
        super(REGISTERED);
    }
}
