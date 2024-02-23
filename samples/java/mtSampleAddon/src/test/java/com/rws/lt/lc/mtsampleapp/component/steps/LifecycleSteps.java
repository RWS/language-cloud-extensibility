package com.rws.lt.lc.mtsampleapp.component.steps;

import com.rws.lt.lc.mtsampleapp.transfer.lifecycle.*;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.Date;

@Slf4j
public class LifecycleSteps extends TestStepsBase {

    @When("I send an app lifecycle event with id {string}")
    public void iSendAppLifecycleEvent(String id) {
        AppLifecycleEvent event = new AppLifecycleEvent(id);
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/app-lifecycle", event, HttpMethod.POST, Object.class);
    }

    @When("I install the app")
    public void iInstallTheApp() {
        InstalledEvent event = new InstalledEvent();
        event.setTimestamp(String.valueOf(new Date()));

        exchange("/v1/app-lifecycle", event, HttpMethod.POST, Object.class);
    }

    @When("I uninstall the app")
    public void iSendAccountUninstall() {
        UninstalledEvent event = new UninstalledEvent();
        event.setTimestamp(String.valueOf(new Date()));
        exchange("/v1/app-lifecycle", event, HttpMethod.POST, Object.class);
    }
}
