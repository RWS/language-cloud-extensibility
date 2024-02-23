package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AddonLifecycleEvent.UNREGISTERED)
public class UnregisteredEvent extends AddonLifecycleEvent {
    public UnregisteredEvent() {
        super(UNREGISTERED);
    }
}
