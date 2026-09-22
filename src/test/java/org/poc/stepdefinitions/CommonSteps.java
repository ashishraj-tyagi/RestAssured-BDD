package org.poc.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.poc.client.ApiClient;
import org.poc.context.ScenarioContext;
import org.poc.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CommonSteps {

    private final ScenarioContext context = ScenarioContext.get();

    @When("I send a GET request to {string}")
    public void sendGetRequest(String path) {
        Response response = ApiClient.get(path);
        context.setResponse(response);
    }

    @When("I send a GET request to {string} with query parameters:")
    public void sendGetRequestWithQueryParams(String path, DataTable dataTable) {
        Map<String, Object> queryParams = toMap(dataTable);
        Response response = ApiClient.get(path, queryParams);
        context.setResponse(response);
    }

    @When("I send a POST request to {string} with body:")
    public void sendPostRequest(String path, String body) {
        context.setRequestBody(body);
        Response response = ApiClient.post(path, body);
        context.setResponse(response);
    }

    @When("I send a PUT request to {string} with body:")
    public void sendPutRequest(String path, String body) {
        context.setRequestBody(body);
        Response response = ApiClient.put(path, body);
        context.setResponse(response);
    }

    @When("I send a PATCH request to {string} with body:")
    public void sendPatchRequest(String path, String body) {
        context.setRequestBody(body);
        Response response = ApiClient.patch(path, body);
        context.setResponse(response);
    }

    @When("I send a DELETE request to {string}")
    public void sendDeleteRequest(String path) {
        Response response = ApiClient.delete(path);
        context.setResponse(response);
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        context.getResponse().then().statusCode(expectedStatusCode);
    }

    @Then("the response header {string} should be {string}")
    public void verifyResponseHeader(String headerName, String expectedValue) {
        String actualValue = context.getResponse().getHeader(headerName);
        assertThat("Header " + headerName, actualValue, equalTo(expectedValue));
    }

    @Then("the response field {string} should be {string}")
    public void verifyResponseField(String jsonPath, String expectedValue) {
        Object actual = context.getResponse().jsonPath().get(jsonPath);
        assertThat("Field " + jsonPath, String.valueOf(actual), equalTo(expectedValue));
    }

    @Then("the response field {string} should not be empty")
    public void verifyResponseFieldNotEmpty(String jsonPath) {
        Object value = context.getResponse().jsonPath().get(jsonPath);
        assertThat("Field " + jsonPath + " should not be empty", value, notNullValue());
        if (value instanceof String stringValue) {
            assertThat("Field " + jsonPath + " should not be blank", stringValue, not(isEmptyString()));
        }
    }

    @Then("the response should contain field {string}")
    public void verifyResponseContainsField(String fieldName) {
        String body = context.getResponse().getBody().asString();
        assertThat("Response should contain field: " + fieldName,
                JsonUtils.parse(body).has(fieldName), is(true));
    }

    @Then("the response body should match schema {string}")
    public void verifyResponseSchema(String schemaPath) {
        context.getResponse().then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    @Then("the response list should contain at least {int} items")
    public void verifyMinimumListSize(int minimumCount) {
        List<?> items = context.getResponse().jsonPath().getList("$");
        assertThat("List size", items.size(), greaterThanOrEqualTo(minimumCount));
    }

    @Given("I store the response field {string} as {string}")
    public void storeResponseField(String jsonPath, String key) {
        Object value = context.getResponse().jsonPath().get(jsonPath);
        context.put(key, value);
    }

    private Map<String, Object> toMap(DataTable dataTable) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            map.put(row.get("parameter"), row.get("value"));
        }
        return map;
    }
}
