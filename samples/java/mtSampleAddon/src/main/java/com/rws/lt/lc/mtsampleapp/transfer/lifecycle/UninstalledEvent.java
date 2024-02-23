package com.rws.lt.lc.mtsampleapp.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AppLifecycleEvent.UNINSTALLED)
public class UninstalledEvent extends AppLifecycleEvent {

    public UninstalledEvent() {
        super(UNINSTALLED);
    }
}
