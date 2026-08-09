package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperUtils {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .setPropertyNamingStrategy(
                            PropertyNamingStrategies.SNAKE_CASE
                    )
                    .configure(
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                            false
                    );

    private ObjectMapperUtils() {
    }

    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }
}