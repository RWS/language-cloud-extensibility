package com.rws.lt.lc.blueprint.transfer.lifecycle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "id")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActivatedEvent.class, name = AddonLifecycleEvent.ACTIVATED),
        @JsonSubTypes.Type(value = RegisteredEvent.class, name = AddonLifecycleEvent.REGISTERED),
        @JsonSubTypes.Type(value = UnregisteredEvent.class, name = AddonLifecycleEvent.UNREGISTERED),
        @JsonSubTypes.Type(value = DeactivatedEvent.class, name = AddonLifecycleEvent.DEACTIVATED)
})
@Getter
@Setter
@NoArgsConstructor
public class AddonLifecycleEvent {
    public static final String ACTIVATED = "ACTIVATED";
    public static final String REGISTERED = "REGISTERED";
    public static final String UNREGISTERED = "UNREGISTERED";
    public static final String DEACTIVATED = "DEACTIVATED";

    public AddonLifecycleEvent(String id) {
        this.id = id;
    }

    @Valid
    @NotNull
    @Pattern(regexp = REGISTERED + "|" + UNREGISTERED + "|" + ACTIVATED + "|" + DEACTIVATED, message = "INVALID")
    private String id;

    @Valid
    @NotNull
    private String timestamp;

}
