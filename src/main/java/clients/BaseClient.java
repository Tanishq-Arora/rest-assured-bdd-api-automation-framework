package clients;

import configs.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import utils.ObjectMapperUtils;

import static constants.BaseClientConstants.*;
import static org.hamcrest.Matchers.lessThan;

public abstract class BaseClient {

    protected static RequestSpecification jsonRequest;
    protected static ResponseSpecification successResponse;

    public static void setup() {

        RestAssured.config =
                RestAssuredConfig.config()
                        .objectMapperConfig(
                                ObjectMapperConfig
                                        .objectMapperConfig()
                                        .jackson2ObjectMapperFactory(
                                                (cls, charset) ->
                                                        ObjectMapperUtils
                                                                .getMapper()
                                        )
                        );


        // Request specification
        jsonRequest = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setBasePath(ConfigReader.getBasePath())
                .addHeader(
                        X_API_KEY,
                        ConfigReader.getAPIKey()
                )
                .addHeader(
                        X_REQRES_ENV,
                        ConfigReader.getBaseEnvironment()
                )
                .addQueryParam(
                        PROJECT_ID,
                        ConfigReader.getProjectId()
                )
                .setContentType(ConfigReader.getContentType())
                .setAccept(ConfigReader.getAcceptType())
                .log(LogDetail.ALL)
                .build();

        // Response specification
        successResponse = new ResponseSpecBuilder()
                .expectContentType(ConfigReader.getContentType())
                .expectResponseTime(lessThan(ConfigReader.getResponseTimeout()))
                .build();

            RestAssured.filters(
                    new RequestLoggingFilter(),
                    new ResponseLoggingFilter()
            );
        // Log request/response only when validation fails
//        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static void cleanup(){
        RestAssured.reset();
    }
}