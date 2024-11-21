package com.rws.lt.lc.blueprint.component.steps;

import com.rws.lt.lc.blueprint.transfer.lifecycle.*;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.UUID;

import static com.rws.lt.lc.blueprint.component.MockComponentTestConfiguration.TEST_ACCOUNT_ID;
import static com.rws.lt.lc.blueprint.component.MockComponentTestConfiguration.TEST_APP_ID;
import static com.rws.lt.lc.blueprint.metadata.AppMetadataConstants.APP_ID_HEADER;
import static com.rws.lt.lc.blueprint.metadata.AppMetadataConstants.DEV_TENANT_ID_HEADER;

@Slf4j
public class LifecycleSteps extends TestStepsBase {

    @When("I send an app lifecycle event with id {string}")
    public void iSendAppLifecycleEvent(String id) {
        AppLifecycleEvent event = new AppLifecycleEvent(id);
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/app-lifecycle", event, getLifeCycleHeaders(), HttpMethod.POST, Object.class);
    }

    @When("I install the app")
    public void iInstallTheApp() {
        InstalledEvent event = new InstalledEvent();
        event.setTimestamp(String.valueOf(new Date()));
        event.setData(new InstalledEventDetails("eu"));
        exchange("/v1/app-lifecycle", event, getLifeCycleHeaders(), HttpMethod.POST, Object.class);
    }

    @When("I register the app")
    public void iRegisterTheApp() {
        var registeredEvent = new RegisteredEvent();
        registeredEvent.setTimestamp(String.valueOf(new Date()));
        var details = new RegisteredEventDetails();
        details.setClientCredentials(new ClientCredentialsTO(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        registeredEvent.setData(details);
        exchange("/v1/app-lifecycle", registeredEvent, getLifeCycleHeaders(), HttpMethod.POST, Object.class);
    }

    @When("I uninstall the app")
    public void iSendAccountUninstall() {
        UninstalledEvent event = new UninstalledEvent();
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/app-lifecycle", event, getLifeCycleHeaders(), HttpMethod.POST, Object.class);
    }

    private static HttpHeaders getLifeCycleHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(DEV_TENANT_ID_HEADER, TEST_ACCOUNT_ID);
        headers.add(APP_ID_HEADER, TEST_APP_ID);
        return headers;
    }
}