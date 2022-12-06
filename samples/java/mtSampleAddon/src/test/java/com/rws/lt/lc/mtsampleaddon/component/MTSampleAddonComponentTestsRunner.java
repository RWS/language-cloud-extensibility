package com.rws.lt.lc.mtsampleaddon.component;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/mtsampleaddonReport.json"},
        glue = {
                "com.rws.lt.lc.mtsampleaddon"
        },
        features = "classpath:features"
)
@ContextConfiguration(classes = CucumberContextConfiguration.class)
public class MTSampleAddonComponentTestsRunner {
}