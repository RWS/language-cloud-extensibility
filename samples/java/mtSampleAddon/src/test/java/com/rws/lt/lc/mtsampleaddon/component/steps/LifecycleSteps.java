package com.rws.lt.lc.mtsampleaddon.component.steps;

import com.rws.lt.lc.mtsampleaddon.transfer.lifecycle.*;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.UUID;

@Slf4j
public class LifecycleSteps extends TestStepsBase {

    @When("I send an addon lifecycle event with id {string}")
    public void iSendAddonLifecycleEvent(String id) {
        AddonLifecycleEvent event = new AddonLifecycleEvent(id);
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/addon-lifecycle", event, HttpMethod.POST, Object.class);
    }

    @When("I activate the addon")
    public void iActivateTheAddon() {
        ActivatedEvent event = new ActivatedEvent();
        event.setTimestamp(String.valueOf(new Date()));
        ActivatedEventDetails details = new ActivatedEventDetails();

        details.setClientCredentials(new ClientCredentialsTO(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        event.setData(details);
        exchange("/v1/addon-lifecycle", event, HttpMethod.POST, Object.class);
    }

    @When("I deactivate the addon")
    public void iSendAccountDeactivate() {
        DeactivatedEvent event = new DeactivatedEvent();
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/addon-lifecycle", event, HttpMethod.POST, Object.class);
    }
}
