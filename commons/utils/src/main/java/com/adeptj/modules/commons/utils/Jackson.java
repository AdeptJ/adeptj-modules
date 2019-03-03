package com.adeptj.modules.commons.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Provides Jackson's serializer {@link ObjectWriter} and deserializer {@link ObjectReader} objects .
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class Jackson {

    private static final ObjectMapper GLOBAL_OBJECT_MAPPER = new ObjectMapper();

    public static ObjectReader objectReader() {
        return GLOBAL_OBJECT_MAPPER.reader();
    }

    public static ObjectWriter objectWriter() {
        return GLOBAL_OBJECT_MAPPER.writer();
    }
}
