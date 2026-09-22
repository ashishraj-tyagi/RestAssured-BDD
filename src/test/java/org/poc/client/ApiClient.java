package org.poc.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.poc.config.ConfigManager;

import java.util.Map;

public final class ApiClient {

    private static RequestSpecification baseSpec;

    private ApiClient() {
    }

    public static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.useRelaxedHTTPSValidation();

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("base.url"))
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter());

        String bypass = System.getenv("VERCEL_AUTOMATION_BYPASS_SECRET");
        if (bypass != null && !bypass.isBlank()) {
            builder.addHeader("x-vercel-protection-bypass", bypass.trim());
        }

        baseSpec = builder.build();

        RestAssured.requestSpecification = baseSpec;
        RestAssured.responseSpecification = RestAssured.expect()
                .time(org.hamcrest.Matchers.lessThan((long) ConfigManager.getInt("request.timeout.ms", 10000)));
    }

    public static Response get(String path) {
        return RestAssured.given()
                .when()
                .get(path);
    }

    public static Response get(String path, Map<String, ?> queryParams) {
        return RestAssured.given()
                .queryParams(queryParams)
                .when()
                .get(path);
    }

    public static Response post(String path, Object body) {
        return RestAssured.given()
                .body(body)
                .when()
                .post(path);
    }

    public static Response put(String path, Object body) {
        return RestAssured.given()
                .body(body)
                .when()
                .put(path);
    }

    public static Response patch(String path, Object body) {
        return RestAssured.given()
                .body(body)
                .when()
                .patch(path);
    }

    public static Response delete(String path) {
        return RestAssured.given()
                .when()
                .delete(path);
    }
}
