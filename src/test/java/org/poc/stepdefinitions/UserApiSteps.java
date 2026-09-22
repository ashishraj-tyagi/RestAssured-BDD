package org.poc.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.poc.client.ApiClient;
import org.poc.context.ScenarioContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserApiSteps {

    private final ScenarioContext context = ScenarioContext.get();

    @When("I fetch all users")
    public void fetchAllUsers() {
        Response response = ApiClient.get("/users");
        context.setResponse(response);
    }

    @When("I fetch user with id {int}")
    public void fetchUserById(int userId) {
        Response response = ApiClient.get("/users/" + userId);
        context.setResponse(response);
    }

    @When("I create a user with name {string} and email {string}")
    public void createUser(String name, String email) {
        String body = """
                {
                  "name": "%s",
                  "username": "%s",
                  "email": "%s"
                }
                """.formatted(name, name.toLowerCase().replace(" ", "."), email);
        context.setRequestBody(body);
        Response response = ApiClient.post("/users", body);
        context.setResponse(response);
    }

    @Then("the response should contain at least {int} users")
    public void verifyMinimumUserCount(int minimumCount) {
        List<?> items = context.getResponse().jsonPath().getList("$");
        assertThat("User count", items.size(), greaterThanOrEqualTo(minimumCount));
    }

    @Then("the user email should be {string}")
    public void verifyUserEmail(String expectedEmail) {
        context.getResponse().then().body("email", equalTo(expectedEmail));
    }
}
