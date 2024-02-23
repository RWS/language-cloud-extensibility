package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AppLifecycleEvent.UNINSTALLED)
public class DeactivatedEvent extends AppLifecycleEvent {

    public DeactivatedEvent() {
        super(UNINSTALLED);
    }
}
