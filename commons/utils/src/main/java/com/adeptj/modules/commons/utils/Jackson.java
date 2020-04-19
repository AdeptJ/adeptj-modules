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

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

/**
 * Provides Jackson's serializer {@link ObjectWriter} and deserializer {@link ObjectReader} objects .
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class Jackson {

    private Jackson() {
    }

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper()
            .enable(INDENT_OUTPUT)
            .setSerializationInclusion(NON_NULL)
            .setDefaultPropertyInclusion(NON_DEFAULT);

    public static ObjectMapper objectMapper() {
        return DEFAULT_OBJECT_MAPPER;
    }

    public static ObjectNode objectNode() {
        return (ObjectNode) objectReader().createObjectNode();
    }

    public static ObjectReader objectReader() {
        return objectMapper().reader();
    }

    public static ObjectWriter objectWriter() {
        return objectMapper().writer();
    }

    public static JsonNode parse(String json) {
        try {
            return objectReader().readTree(json);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static JsonNode parse(byte[] json) {
        try {
            return objectReader().readTree(json);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static byte[] asBytes(Object object) {
        try {
            return objectWriter().writeValueAsBytes(object);
        } catch (JsonProcessingException ex) {
            throw new JacksonException(ex);
        }
    }

    public static <T> T readValue(byte[] bytes, Class<T> valueType) {
        try {
            return Jackson.objectReader().forType(valueType).readValue(bytes);
        } catch (IOException ex) {
            throw new JacksonException(ex);
        }
    }

    public static String asString(Object object) {
        try {
            return objectWriter().writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new JacksonException(ex);
        }
    }

    public static String getAsString(JsonNode node, String property) {
        if (node == null) {
            return null;
        }
        if (StringUtils.isEmpty(property)) {
            return null;
        }
        return node.has(property) ? node.get(property).asText() : null;
    }

    public static boolean isPropertyTrue(JsonNode node, String property) {
        if (node == null) {
            return false;
        }
        if (StringUtils.isEmpty(property)) {
            return false;
        }
        return node.has(property) && node.get(property).asBoolean();
    }

}
