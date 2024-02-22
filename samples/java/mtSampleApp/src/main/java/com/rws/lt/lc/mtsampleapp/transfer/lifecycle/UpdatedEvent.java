package com.rws.lt.lc.mtsampleapp.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AppLifecycleEvent.UPDATED)
public class UpdatedEvent extends AppLifecycleEvent {

    public UpdatedEvent() { super(UPDATED);}
}
