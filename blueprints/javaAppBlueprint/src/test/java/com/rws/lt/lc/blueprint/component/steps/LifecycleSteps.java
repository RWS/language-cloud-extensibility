package com.rws.lt.lc.blueprint.component.steps;

import com.rws.lt.lc.blueprint.transfer.lifecycle.*;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.UUID;

@Slf4j
public class LifecycleSteps extends TestStepsBase {

    @When("I send an app lifecycle event with id {string}")
    public void iSendAppLifecycleEvent(String id) {
        AppLifecycleEvent event = new AppLifecycleEvent(id);
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/app-lifecycle", event, HttpMethod.POST, Object.class);
    }

    @When("I activate the app")
    public void iActivateTheApp() {
        InstalledEvent event = new InstalledEvent();
        event.setTimestamp(String.valueOf(new Date()));
        ActivatedEventDetails details = new ActivatedEventDetails();

        details.setClientCredentials(new ClientCredentialsTO(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        event.setData(details);
        exchange("/v1/app-lifecycle", event, HttpMethod.POST, Object.class);
    }

    @When("I deactivate the app")
    public void iSendAccountDeactivate() {
        DeactivatedEvent event = new DeactivatedEvent();
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/app-lifecycle", event, HttpMethod.POST, Object.class);
    }
}
