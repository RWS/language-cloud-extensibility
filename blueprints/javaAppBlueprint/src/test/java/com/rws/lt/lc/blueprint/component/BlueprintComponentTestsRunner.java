package com.rws.lt.lc.blueprint.component;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/blueprintReport.json"},
        glue = {
                "com.rws.lt.lc.blueprint"
        },
        features = "classpath:features"
)
@ContextConfiguration(classes = CucumberContextConfiguration.class)
public class BlueprintComponentTestsRunner {
}