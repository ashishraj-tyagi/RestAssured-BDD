package org.poc.stepdefinitions;

import io.cucumber.java.en.Given;
import org.poc.client.ApiClient;

public class HealthSteps {

    @Given("the API is available")
    public void verifyApiIsAvailable() {
        ApiClient.init();
    }
}
