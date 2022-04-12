package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AddonLifecycleEvent.DEACTIVATED)
public class DeactivatedEvent extends AddonLifecycleEvent {

    public DeactivatedEvent() {
        super(DEACTIVATED);
    }
}
