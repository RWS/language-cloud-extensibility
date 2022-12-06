package com.rws.lt.lc.mtsampleaddon.component.steps;

import com.rws.lt.lc.mtsampleaddon.component.ScenarioStorage;
import com.rws.lt.lc.mtsampleaddon.transfer.Descriptor;
import com.rws.lt.lc.mtsampleaddon.transfer.DescriptorExtension;
import com.rws.lt.lc.mtsampleaddon.transfer.HealthStatus;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.collection.IsArrayWithSize;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.Is;
import org.hamcrest.core.StringContains;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.Assert.assertThat;

@Slf4j
public class MedatadaTestSteps extends TestStepsBase {


    @When("^I get the add-on descriptor")
    public void getDescriptor() {
        ResponseEntity<Descriptor> response = getRestTemplate().exchange(getBaseUrl() + "/v1/descriptor", HttpMethod.GET, null, Descriptor.class);
        ScenarioStorage.setLastResponse(response);
    }

    @And("The descriptor name should be {string}")
    public void andTheNameIs(String name) {
        Descriptor descriptor = ScenarioStorage.getLastResponse();
        assertThat(name, Is.is(descriptor.getName()));
    }

    @And("The descriptor version is {string}")
    public void andTheVersionIsValid(String expectedVersion) {
        Descriptor descriptor = ScenarioStorage.getLastResponse();
        assertThat(descriptor.getVersion(), Is.is(expectedVersion));
    }

    @And("The descriptor baseUrl contains {string}")
    public void andTheBaseUrlContains(String s) {
        Descriptor descriptor = ScenarioStorage.getLastResponse();
        assertThat(descriptor.getBaseUrl(), StringContains.containsString(s));
    }

    @And("The descriptor has {int} extensions")
    public void andHasExtensions(int endpointCount) {
        Descriptor descriptor = ScenarioStorage.getLastResponse();
        assertThat(descriptor.getExtensions(), IsArrayWithSize.arrayWithSize(endpointCount));
    }

    @And("Extension {int} has the endpoints")
    public void andHasEndpoints(int index, DataTable endpointsDatatable) {
        Descriptor descriptor = ScenarioStorage.getLastResponse();
        DescriptorExtension extension = descriptor.getExtensions()[0];
        assertThat(extension.getConfiguration(), IsMapContaining.hasKey("endpoints"));
        Map<String, String> endpoints = (Map<String, String>) extension.getConfiguration().get("endpoints");
        Map<String, String> expectedEndpoints = endpointsDatatable.asMap(String.class, String.class);
        assertThat(endpoints.size(), Is.is(expectedEndpoints.size()));

        for (Map.Entry<String, String> expectedEntry : expectedEndpoints.entrySet()) {
            assertThat(endpoints, IsMapContaining.hasEntry(expectedEntry.getKey(), expectedEntry.getValue()));
        }
    }

    @When("I get the add-on health")
    public void getHealth() {
        ResponseEntity<HealthStatus> response = getRestTemplate().exchange(getBaseUrl() + "/v1/health", HttpMethod.GET, null, HealthStatus.class);
        ScenarioStorage.setLastResponse(response);
    }


    @When("I get the add-on documentation")
    public void getDocumentation() {
        ResponseEntity<HealthStatus> response = getRestTemplate().exchange(getBaseUrl() + "/v1/documentation", HttpMethod.GET, null, HealthStatus.class);
        ScenarioStorage.setLastResponse(response);
    }

    @When("The add-on health response is true")
    public void checkHealth() {
        HealthStatus lastResponse = ScenarioStorage.getLastResponse();
        assertThat(lastResponse.isOk(), Is.is(true));
    }

}
