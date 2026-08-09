package models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder
@Jacksonized
public class TaskUpdateResponseData {

    String id;

    String collectionId;

    Integer projectId;

    Integer appUserId;

    Integer createdBy;

    Instant createdAt;

    Instant updatedAt;

    Instant deletedAt;

    TaskUpdateData data;
}