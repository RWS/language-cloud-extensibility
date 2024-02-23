package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonTypeName(AddonLifecycleEvent.ACTIVATED)
public class ActivatedEvent extends AddonLifecycleEvent {

    public ActivatedEvent() {
        super(ACTIVATED);
    }

    @Valid
    @NotNull
    private ActivatedEventDetails data;

}
