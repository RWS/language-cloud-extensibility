package com.rws.lt.lc.mtsampleaddon.component.steps;

import com.rws.lt.lc.mtsampleaddon.component.ScenarioStorage;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorDetail;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import org.hamcrest.core.StringContains;
import org.junit.Assert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GenericTestSteps {

    @When("Last response status code is {int}")
    public void checkLastResponseStatusCode(int statusCode) {
        assertThat(ScenarioStorage.getLastStatusCode(), is(statusCode));
    }


    @When("Last error code is {string}")
    public void checkLastErrorCode(String errorCode) {
        assertThat(ScenarioStorage.getLastError().getErrorCode(), is(errorCode));
    }

    @And("Last error contains {string}")
    public void lastErrorContains(String expected) {
        boolean correctError = false;
        ErrorResponse lastError = ScenarioStorage.getLastError();
        for (ErrorDetail details : lastError.getDetails()) {
            correctError |= details.getName().contains(expected);
        }
        Assert.assertTrue("Last response is " + lastError + " and it doesn't contain the expected error name " + expected, correctError);
    }

    @And("Last error message contains {string}")
    public void lastErrorMessageContains(String expected) {
        ErrorResponse lastError = ScenarioStorage.getLastError();
        assertThat("Last error was " + lastError, lastError.getMessage(), StringContains.containsString(expected));
    }
}
