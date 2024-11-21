package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonTypeName(AppLifecycleEvent.INSTALLED)
public class InstalledEvent extends AppLifecycleEvent {

    public InstalledEvent() {
        super(INSTALLED);
    }

    @Valid
    @NotNull
    private InstalledEventDetails data;

}
