package clients;

import configs.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import static constants.BaseClientConstants.*;
import static org.hamcrest.Matchers.lessThan;

public abstract class BaseClient {

    protected static RequestSpecification jsonRequest;
    protected static ResponseSpecification successResponse;

    @BeforeAll
    static void setup() {

        // Request specification
        jsonRequest = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setBasePath(ConfigReader.getBasePath())
                .addHeader(
                        X_API_KEY,
                        ConfigReader.getAPIKey()
                )
                .setContentType(ConfigReader.getContentType())
                .setAccept(ConfigReader.getAcceptType())
                .log(LogDetail.METHOD)
                .build();

        // Response specification
        successResponse = new ResponseSpecBuilder()
                .expectContentType(ConfigReader.getContentType())
                .expectResponseTime(lessThan(ConfigReader.getResponseTimeout()))
                .build();

        // Log request/response only when validation fails
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}