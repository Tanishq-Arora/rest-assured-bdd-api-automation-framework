package clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class TicketClient extends BaseClient {

    // ==========================================
    // GET COLLECTION RECORDS
    // ==========================================

    public Response getRecords(String collection) {

        return given()
                .spec(jsonRequest)

                .when()
                .get("/collections/{collection}/records", collection);
    }


    // ==========================================
    // GET RECORD BY ID
    // ==========================================

    public Response getRecordById(
            String collection,
            String recordId) {

        return given()
                .spec(jsonRequest)

                .when()
                .get(
                        "/collections/{collection}/records/{id}",
                        collection,
                        recordId
                );
    }


    // ==========================================
    // CREATE RECORD
    // ==========================================

    public Response createRecord(
            String collection,
            Object requestBody) {

        return given()
                .spec(jsonRequest)
                .body(requestBody)

                .when()
                .post(
                        "/collections/{collection}/records",
                        collection
                );
    }


    // ==========================================
    // UPDATE RECORD
    // ==========================================

    public Response updateRecord(
            String collection,
            String recordId,
            Object requestBody) {

        return given()
                .spec(jsonRequest)
                .body(requestBody)

                .when()
                .put(
                        "/collections/{collection}/records/{id}",
                        collection,
                        recordId
                );
    }


    // ==========================================
    // PATCH RECORD
    // ==========================================

    public Response patchRecord(
            String collection,
            String recordId,
            Object requestBody) {

        return given()
                .spec(jsonRequest)
                .body(requestBody)

                .when()
                .patch(
                        "/collections/{collection}/records/{id}",
                        collection,
                        recordId
                );
    }


    // ==========================================
    // DELETE RECORD
    // ==========================================

    public Response deleteRecord(
            String collection,
            String recordId) {

        return given()
                .spec(jsonRequest)

                .when()
                .delete(
                        "/collections/{collection}/records/{id}",
                        collection,
                        recordId
                );
    }
}
