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
        @JsonSubTypes.Type(value = InstalledEvent.class, name = AppLifecycleEvent.ACTIVATED),
        @JsonSubTypes.Type(value = InstalledEvent.class, name = AppLifecycleEvent.INSTALLED),
        @JsonSubTypes.Type(value = RegisteredEvent.class, name = AppLifecycleEvent.REGISTERED),
        @JsonSubTypes.Type(value = UpdatedEvent.class, name = AppLifecycleEvent.UPDATED),
        @JsonSubTypes.Type(value = UnregisteredEvent.class, name = AppLifecycleEvent.UNREGISTERED),
        @JsonSubTypes.Type(value = UninstalledEvent.class, name = AppLifecycleEvent.DEACTIVATED),
        @JsonSubTypes.Type(value = UninstalledEvent.class, name = AppLifecycleEvent.UNINSTALLED)
})
@Getter
@Setter
@NoArgsConstructor
public class AppLifecycleEvent {
    public static final String ACTIVATED = "ACTIVATED";
    public static final String INSTALLED = "INSTALLED";
    public static final String REGISTERED = "REGISTERED";
    public static final String UPDATED = "UPDATED";
    public static final String UNREGISTERED = "UNREGISTERED";
    public static final String DEACTIVATED = "DEACTIVATED";
    public static final String UNINSTALLED = "UNINSTALLED";

    public AppLifecycleEvent(String id) {
        this.id = id;
    }

    @Valid
    @NotNull
    @Pattern(regexp = REGISTERED + "|" + UPDATED + "|" + UNREGISTERED + "|" + INSTALLED + "|" + ACTIVATED + "|" + UNINSTALLED + "|" + DEACTIVATED, message = "INVALID")
    private String id;

    @Valid
    @NotNull
    private String timestamp;

}