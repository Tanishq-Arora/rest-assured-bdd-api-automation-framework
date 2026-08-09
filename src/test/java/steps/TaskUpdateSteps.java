package steps;

import constants.StepConstants;
import context.ScenarioContext;
import dataFactories.TaskUpdateFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import models.TaskUpdateRequest;
import models.TaskUpdateResponse;
import net.serenitybdd.annotations.Steps;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TaskUpdateSteps {
    @Steps
    private ScenarioContext context;
    // =========================================================
    // BACKGROUND
    // =========================================================

    @Given("the task updates collection is available")
    public void taskUpdatesCollectionIsAvailable() {

        Response response =
                context.getTicketClient()
                        .getRecords(StepConstants.COLLECTION_NAME);

        assertThat(response.statusCode())
                .as("Task Updates collection should be available")
                .isEqualTo(200);
    }


    // =========================================================
    // CREATE
    // =========================================================

    @Given("I have a valid task update request")
    public void iHaveAValidTaskUpdateRequest() {

        TaskUpdateRequest request =
                TaskUpdateFactory.random();

        context.setTaskUpdateRequest(request);
    }


    @When("I create the task update")
    public void iCreateTheTaskUpdate() {

        Response response =
                context.getTicketClient()
                        .createRecord(
                                StepConstants.COLLECTION_NAME,
                                context.getTaskUpdateRequest()
                        );

        context.setResponse(response);
    }


    @Then("the task update should be created successfully")
    public void taskUpdateShouldBeCreatedSuccessfully() {

        TaskUpdateResponse response =
                context.getResponse()
                        .then()
                        .extract()
                        .as(TaskUpdateResponse.class);

        assertThat(response.getData())
                .as("Response data should not be null")
                .isNotNull();

        assertThat(response.getData().getId())
                .as("Created task update should have an ID")
                .isNotNull();
    }


    // =========================================================
    // COMMON STATUS CODE
    // =========================================================

    @Then("the response status code should be {int}")
    public void responseStatusCodeShouldBe(
            int expectedStatusCode) {

        assertThat(context.getResponse().statusCode())
                .as("Unexpected response status code")
                .isEqualTo(expectedStatusCode);
    }


    // =========================================================
    // RETRIEVE ALL
    // =========================================================

    @When("I retrieve all task updates")
    public void iRetrieveAllTaskUpdates() {

        Response response =
                context.getTicketClient()
                        .getRecords(
                                StepConstants.COLLECTION_NAME
                        );

        context.setResponse(response);
    }


    @Then("the task updates response should contain records")
    public void taskUpdatesResponseShouldContainRecords() {

        Object data =
                context.getResponse()
                        .jsonPath()
                        .get("data");

        assertThat(data)
                .as("Task updates response should contain data")
                .isNotNull();
    }


    // =========================================================
    // RETRIEVE BY ID
    // =========================================================

    @Given("a task update exists")
    public void aTaskUpdateExists() {

        TaskUpdateRequest request =
                TaskUpdateFactory.random();

        context.setTaskUpdateRequest(request);

        Response response =
                context.getTicketClient()
                        .createRecord(
                                StepConstants.COLLECTION_NAME,
                                request
                        );

        context.setResponse(response);

        assertThat(response.statusCode())
                .as("Failed to create prerequisite task update")
                .isEqualTo(201);
    }


    @When("I retrieve the task update by its ID")
    public void iRetrieveTheTaskUpdateByItsId() {

        String taskUpdateId =
                context.getResponse()
                        .jsonPath()
                        .getString("data.id");

        Response response =
                context.getTicketClient()
                        .getRecordById(
                                StepConstants.COLLECTION_NAME,
                                taskUpdateId
                        );

        context.setResponse(response);
    }


    @Then("the task update details should be returned")
    public void taskUpdateDetailsShouldBeReturned() {

        TaskUpdateResponse response =
                context.getResponse()
                        .then()
                        .extract()
                        .as(TaskUpdateResponse.class);

        assertThat(response.getData())
                .as("Response data should not be null")
                .isNotNull();

        assertThat(response.getData().getId())
                .as("Task update ID should not be null")
                .isNotNull();

        assertThat(response.getData().getData())
                .as("Task update details should not be null")
                .isNotNull();
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Given("I have updated task details")
    public void iHaveUpdatedTaskDetails() {

        TaskUpdateRequest updatedRequest =
                TaskUpdateFactory.random();

        context.setTaskUpdateRequest(
                updatedRequest
        );
    }


    @When("I update the task update")
    public void iUpdateTheTaskUpdate() {

        String taskUpdateId =
                context.getResponse()
                        .jsonPath()
                        .getString("data.id");

        Response response =
                context.getTicketClient()
                        .updateRecord(
                                StepConstants.COLLECTION_NAME,
                                taskUpdateId,
                                context.getTaskUpdateRequest()
                        );

        context.setResponse(response);
    }


    @Then("the task update should contain the updated details")
    public void taskUpdateShouldContainUpdatedDetails() {

        TaskUpdateResponse response =
                context.getResponse()
                        .then()
                        .extract()
                        .as(TaskUpdateResponse.class);

        assertThat(response.getData())
                .as("Response data should not be null")
                .isNotNull();

        assertThat(response.getData().getData())
                .as("Updated task details should not be null")
                .isNotNull();

        assertThat(response.getData().getData().getTitle())
                .as("Task title should be updated")
                .isEqualTo(
                        context.getTaskUpdateRequest()
                                .getData()
                                .getTitle()
                );

        assertThat(response.getData().getData().getPriority())
                .as("Task priority should be updated")
                .isEqualTo(
                        context.getTaskUpdateRequest()
                                .getData()
                                .getPriority()
                );

        assertThat(response.getData().getData().getCompleted())
                .as("Task completed status should be updated")
                .isEqualTo(
                        context.getTaskUpdateRequest()
                                .getData()
                                .getCompleted()
                );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @When("I delete the task update")
    public void iDeleteTheTaskUpdate() {

        String taskUpdateId =
                context.getResponse()
                        .jsonPath()
                        .getString("data.id");

        Response response =
                context.getTicketClient()
                        .deleteRecord(
                                StepConstants.COLLECTION_NAME,
                                taskUpdateId
                        );

        context.setResponse(response);
    }


    // =========================================================
    // PATCH
    // =========================================================

    @Given("I have partially updated task details")
    public void iHavePartiallyUpdatedTaskDetails() {
            context.setTaskUpdateRequest(
                    TaskUpdateFactory.partialUpdate()
            );
    }


    @When("I patch the task update")
    public void iPatchTheTaskUpdate() {

        String taskUpdateId =
                context.getResponse()
                        .jsonPath()
                        .getString("data.id");

        Response response =
                context.getTicketClient()
                        .patchRecord(
                                StepConstants.COLLECTION_NAME,
                                taskUpdateId,
                                context.getTaskUpdateRequest()
                        );

        context.setResponse(response);
    }


    @Then("the task update should contain the patched details")
    public void taskUpdateShouldContainThePatchedDetails() {

        Response response = context.getResponse();

        assertThat(response.jsonPath().getInt("data.priority"))
                .as("Patched task priority should match the request")
                .isEqualTo(
                        context.getTaskUpdateRequest()
                                .getData()
                                .getPriority()
                );

        assertThat(response.jsonPath().getString("updatedAt"))
                .as("PATCH response should contain updatedAt")
                .isNotNull()
                .isNotBlank();
    }

    // =========================================================
// PENDING TASK
// =========================================================

    @Given("I have a pending task update request")
    public void iHaveAPendingTaskUpdateRequest() {

        context.setTaskUpdateRequest(
                TaskUpdateFactory.pending()
        );
    }

    @Then("the task update should be pending")
    public void taskUpdateShouldBePending() {

        TaskUpdateResponse response =
                context.getResponse()
                        .then()
                        .extract()
                        .as(TaskUpdateResponse.class);

        assertThat(response.getData())
                .as("Response data should not be null")
                .isNotNull();

        assertThat(response.getData().getData())
                .as("Task data should not be null")
                .isNotNull();

        assertThat(response.getData().getData().getCompleted())
                .as("Task should be pending")
                .isFalse();
    }


// =========================================================
// RETRIEVE - INVALID ID
// =========================================================

    @When("I retrieve a task update using an invalid ID")
    public void iRetrieveATaskUpdateUsingAnInvalidId() {

        Response response =
                context.getTicketClient()
                        .getRecordById(
                                StepConstants.COLLECTION_NAME,
                                StepConstants.INVALID_RECORD_ID
                        );

        context.setResponse(response);
    }


// =========================================================
// UPDATE - INVALID ID
// =========================================================

    @When("I update a task update using an invalid ID")
    public void iUpdateATaskUpdateUsingAnInvalidId() {

        Response response =
                context.getTicketClient()
                        .updateRecord(
                                StepConstants.COLLECTION_NAME,
                                StepConstants.INVALID_RECORD_ID,
                                context.getTaskUpdateRequest()
                        );

        context.setResponse(response);
    }


// =========================================================
// PATCH - INVALID ID
// =========================================================

    @When("I patch a task update using an invalid ID")
    public void iPatchATaskUpdateUsingAnInvalidId() {

        Response response =
                context.getTicketClient()
                        .patchRecord(
                                StepConstants.COLLECTION_NAME,
                                StepConstants.INVALID_RECORD_ID,
                                context.getTaskUpdateRequest()
                        );

        context.setResponse(response);
    }


// =========================================================
// DELETE - INVALID ID
// =========================================================

    @When("I delete a task update using an invalid ID")
    public void iDeleteATaskUpdateUsingAnInvalidId() {

        Response response =
                context.getTicketClient()
                        .deleteRecord(
                                StepConstants.COLLECTION_NAME,
                                StepConstants.INVALID_RECORD_ID
                        );

        context.setResponse(response);
    }
}