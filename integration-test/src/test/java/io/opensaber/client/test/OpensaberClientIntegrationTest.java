package io.opensaber.client.test;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {
            "pretty",
            "html:target/cucumber-results/integration-test-reports",
            "json:target/cucumber-results/registry_it_report.json",
            "junit:target/cucumber-results/registry_it_report.xml"
    },
    tags = {"@opensaberclient"},
    features = "src/test/java/io/opensaber/client/test",
    glue = {"io.opensaber.client.test"})
public class OpensaberClientIntegrationTest {

}
