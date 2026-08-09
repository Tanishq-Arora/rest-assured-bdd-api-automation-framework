package dataFactories;

import com.github.javafaker.Faker;
import models.*;

public final class TaskUpdateFactory {

    private static final Faker FAKER = new Faker();

    private TaskUpdateFactory() {
        // Utility class
    }

    public static TaskUpdateRequest random() {

        return TaskUpdateRequest.builder()
                .data(
                        TaskUpdateData.builder()
                                .title(FAKER.lorem().sentence(3))
                                .priority(
                                        FAKER.number()
                                                .numberBetween(1, 5)
                                )
                                .completed(
                                        FAKER.bool().bool()
                                )
                                .build()
                )
                .build();
    }

    public static TaskUpdateRequest completed() {

        return TaskUpdateRequest.builder()
                .data(
                        TaskUpdateData.builder()
                                .title(FAKER.lorem().sentence(3))
                                .priority(
                                        FAKER.number()
                                                .numberBetween(1, 5)
                                )
                                .completed(true)
                                .build()
                )
                .build();
    }

    public static TaskUpdateRequest pending() {

        return TaskUpdateRequest.builder()
                .data(
                        TaskUpdateData.builder()
                                .title(FAKER.lorem().sentence(3))
                                .priority(
                                        FAKER.number()
                                                .numberBetween(1, 5)
                                )
                                .completed(false)
                                .build()
                )
                .build();
    }

    public static TaskUpdateRequest partialUpdate() {

        return TaskUpdateRequest.builder()
                .data(
                        TaskUpdateData.builder()
                                .priority(FAKER.number()
                                        .numberBetween(1, 5))
                                .build()
                )
                .build();
    }
}
