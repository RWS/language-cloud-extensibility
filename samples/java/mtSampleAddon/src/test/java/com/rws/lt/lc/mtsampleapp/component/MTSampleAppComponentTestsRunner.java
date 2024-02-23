package com.rws.lt.lc.mtsampleapp.component;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/mtsampleappReport.json"},
        glue = {
                "com.rws.lt.lc.mtsampleapp"
        },
        features = "classpath:features"
)
@ContextConfiguration(classes = CucumberContextConfiguration.class)
public class MTSampleAppComponentTestsRunner {
}