package com.rws.lt.lc.blueprint.component;

import com.rws.lt.lc.blueprint.BlueprintApplication;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class is used by Cucumber as context for each test steps definitions.
 * Only server.port is made random by {@link SpringBootTest}.
 * The property management.port is made random in application-mock.yml.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BlueprintApplication.class)
@ActiveProfiles("mock")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@Slf4j
public class CucumberContextConfiguration {

    @Before
    public void before(Scenario scenario) {
        LOGGER.info("Creating a new scenario storage for id: {}.", scenario.getId());
        ScenarioStorage.newStorage();
    }

    @After
    public void after() {
        LOGGER.info("Removing the scenario storage.");
        ScenarioStorage.reset();
    }
}
