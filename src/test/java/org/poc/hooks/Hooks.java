package org.poc.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.poc.client.ApiClient;
import org.poc.context.ScenarioContext;

public class Hooks {

    @Before(order = 0)
    public void beforeAllScenarios() {
        ApiClient.init();
    }

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        Allure.getLifecycle().updateTestCase(result -> result.setName(scenario.getName()));
    }

    @After
    public void afterScenario(Scenario scenario) {
        ScenarioContext context = ScenarioContext.get();

        if (context.getRequestBody() != null) {
            Allure.addAttachment("Request Body", "application/json", context.getRequestBody());
        }

        if (context.getResponse() != null) {
            Allure.addAttachment(
                    "Response Body",
                    "application/json",
                    context.getResponse().getBody().asPrettyString()
            );
        }

        ScenarioContext.clear();
    }
}
