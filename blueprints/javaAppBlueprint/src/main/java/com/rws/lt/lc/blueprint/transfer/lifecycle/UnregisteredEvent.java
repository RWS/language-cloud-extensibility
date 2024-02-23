package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AppLifecycleEvent.UNREGISTERED)
public class UnregisteredEvent extends AppLifecycleEvent {
    public UnregisteredEvent() {
        super(UNREGISTERED);
    }
}
