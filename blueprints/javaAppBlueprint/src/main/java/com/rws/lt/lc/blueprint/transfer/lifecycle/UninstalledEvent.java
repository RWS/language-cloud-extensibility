package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AppLifecycleEvent.UNREGISTERED)
public class UninstalledEvent extends AppLifecycleEvent {
    public UninstalledEvent() {
        super(UNREGISTERED);
    }
}
