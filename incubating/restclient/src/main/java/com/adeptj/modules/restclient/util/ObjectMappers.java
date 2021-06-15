package com.adeptj.modules.restclient.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ObjectMappers {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(NON_NULL)
            .setDefaultPropertyInclusion(NON_DEFAULT);

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static ObjectReader getReader() {
        return MAPPER.reader();
    }

    public static <T> ObjectReader getReader(Class<T> responseType) {
        return getReader().forType(responseType);
    }

    public static ObjectWriter getWriter() {
        return MAPPER.writer();
    }

    public static <T> String serialize(T object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return (String) object;
        }
        if (object instanceof byte[]) {
            return new String((byte[]) object, UTF_8);
        }
        try {
            return getWriter().writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> String serializePrettyPrint(T object) {
        try {
            return getWriter().withDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> valueType) {
        try {
            return getReader(valueType).readValue(bytes);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
