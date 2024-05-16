package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonTypeName(AppLifecycleEvent.UPDATED)
public class UpdatedEvent extends AppLifecycleEvent {
    public UpdatedEvent() {
        super(UPDATED);
    }

    @Valid
    @NotNull
    private RegisteredEventDetails data;
}
