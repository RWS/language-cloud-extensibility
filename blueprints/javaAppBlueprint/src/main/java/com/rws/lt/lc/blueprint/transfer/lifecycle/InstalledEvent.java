package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName(AppLifecycleEvent.INSTALLED)
public class InstalledEvent extends AppLifecycleEvent {

    public InstalledEvent() {
        super(INSTALLED);
    }

}
