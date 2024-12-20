package com.rws.lt.lc.mtsampleapp.component.steps;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rws.lt.lc.mtsampleapp.component.ScenarioStorage;
import com.rws.lt.lc.mtsampleapp.transfer.HealthStatus;
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


    @When("^I get the app descriptor")
    public void getDescriptor() {
        ResponseEntity<ObjectNode> response = getRestTemplate().exchange(getBaseUrl() + "/v1/descriptor", HttpMethod.GET, null, ObjectNode.class);
        ScenarioStorage.setLastResponse(response);
    }

    @And("The descriptor name should be {string}")
    public void andTheNameIs(String name) {
        ObjectNode descriptor = ScenarioStorage.getLastResponse();
        assertThat(descriptor.get("name").asText(), Is.is(name));
    }

    @And("The descriptor version is {string}")
    public void andTheVersionIsValid(String expectedVersion) {
        ObjectNode descriptor = ScenarioStorage.getLastResponse();
        assertThat(descriptor.get("version").asText(), Is.is(expectedVersion));
    }

    @And("The descriptor has {int} extensions")
    public void andHasExtensions(int endpointCount) {
        ObjectNode descriptor = ScenarioStorage.getLastResponse();
        assertThat(descriptor.get("extensions").size(), Is.is(endpointCount));
    }

    @When("I get the app health")
    public void getHealth() {
        ResponseEntity<HealthStatus> response = getRestTemplate().exchange(getBaseUrl() + "/v1/health", HttpMethod.GET, null, HealthStatus.class);
        ScenarioStorage.setLastResponse(response);
    }


    @When("I get the app documentation")
    public void getDocumentation() {
        ResponseEntity<HealthStatus> response = getRestTemplate().exchange(getBaseUrl() + "/v1/documentation", HttpMethod.GET, null, HealthStatus.class);
        ScenarioStorage.setLastResponse(response);
    }

    @When("The app health response is true")
    public void checkHealth() {
        HealthStatus lastResponse = ScenarioStorage.getLastResponse();
        assertThat(lastResponse.isOk(), Is.is(true));
    }

}
