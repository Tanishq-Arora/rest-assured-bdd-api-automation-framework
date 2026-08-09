package context;

import clients.TicketClient;
import io.restassured.response.Response;
import lombok.Setter;
import models.TaskUpdateRequest;

public class ScenarioContext {

    private final TicketClient ticketClient =
            new TicketClient();

    @Setter
    private Response response;

    @Setter
    private TaskUpdateRequest taskUpdateRequest;

    public TicketClient getTicketClient() {
        return ticketClient;
    }

    public Response getResponse() {
        return response;
    }

    public TaskUpdateRequest getTaskUpdateRequest() {
        return taskUpdateRequest;
    }

}