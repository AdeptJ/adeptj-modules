/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Provides Jackson's serializer {@link ObjectWriter} and deserializer {@link ObjectReader} objects.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class Jackson {

    private Jackson() {
    }

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper()
            .enable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(NON_NULL)
            .setDefaultPropertyInclusion(NON_DEFAULT);

    public static ObjectMapper objectMapper() {
        return DEFAULT_OBJECT_MAPPER;
    }

    public static ObjectNode objectNode() {
        return (ObjectNode) Jackson.objectReader().createObjectNode();
    }

    public static ObjectReader objectReader() {
        return Jackson.objectMapper().reader();
    }

    public static ObjectWriter objectWriter() {
        return Jackson.objectMapper().writer();
    }

    public static JsonNode parse(String json) {
        try {
            return Jackson.objectReader().readTree(json);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static JsonNode parse(byte[] json) {
        try {
            return Jackson.objectReader().readTree(json);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> byte[] serializeToBytes(T object) {
        try {
            return Jackson.objectWriter().writeValueAsBytes(object);
        } catch (JsonProcessingException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> String serialize(T object) {
        try {
            return Jackson.objectWriter().writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> valueType) {
        try {
            return Jackson.objectReader().forType(valueType).readValue(bytes);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T deserialize(String data, Class<T> valueType) {
        try {
            return Jackson.objectReader().forType(valueType).readValue(data);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static @Nullable String asString(JsonNode node, String property) {
        JsonNode jn = Jackson.resolveProperty(node, property);
        return jn == null ? null : jn.asText();
    }

    public static int asInt(JsonNode node, String property, int defaultValue) {
        JsonNode jn = Jackson.resolveProperty(node, property);
        return jn == null ? defaultValue : jn.asInt();
    }

    public static long asLong(JsonNode node, String property, long defaultValue) {
        JsonNode jn = Jackson.resolveProperty(node, property);
        return jn == null ? defaultValue : jn.asLong();
    }

    public static boolean asBoolean(JsonNode node, String property) {
        JsonNode jn = Jackson.resolveProperty(node, property);
        return jn != null && jn.asBoolean();
    }

    private static JsonNode resolveProperty(JsonNode node, String property) {
        if (node == null || StringUtils.isEmpty(property)) {
            return null;
        }
        return node.get(property);
    }
}