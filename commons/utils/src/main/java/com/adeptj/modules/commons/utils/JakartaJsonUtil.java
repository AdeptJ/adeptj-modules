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

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriterFactory;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.ByteArrayInputStream;

import static com.adeptj.modules.commons.utils.Constants.UTF8;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Provides Jakarta {@link Jsonb} and other objects from Jakarta Json-P plus some utility methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JakartaJsonUtil {
    
    private JakartaJsonUtil() {
    }

    private static final Jsonb JSONB;

    private static final JsonReaderFactory READER_FACTORY;

    private static final JsonWriterFactory WRITER_FACTORY;

    private static final JsonBuilderFactory JSON_BUILDER_FACTORY;

    static {
        JSONB = JsonbBuilder.create(new JsonbConfig().withEncoding(UTF8));
        READER_FACTORY = Json.createReaderFactory(null);
        WRITER_FACTORY = Json.createWriterFactory(null);
        JSON_BUILDER_FACTORY = Json.createBuilderFactory(null);
    }

    public static Jsonb getJsonb() {
        return JSONB;
    }

    public static JsonReaderFactory getJsonReaderFactory() {
        return READER_FACTORY;
    }

    public static JsonWriterFactory getJsonWriterFactory() {
        return WRITER_FACTORY;
    }

    public static JsonBuilderFactory getJsonBuilderFactory() {
        return JSON_BUILDER_FACTORY;
    }

    public static <T> String serialize(T object) {
        return JakartaJsonUtil.getJsonb().toJson(object);
    }

    public static <T> byte[] serializeToBytes(T object) {
        return JakartaJsonUtil.serialize(object).getBytes(UTF_8);
    }

    public static <T> T deserialize(byte[] data, Class<T> type) {
        return JakartaJsonUtil.getJsonb().fromJson(new ByteArrayInputStream(data), type);
    }

    public static <T> T deserialize(String data, Class<T> type) {
        return JakartaJsonUtil.getJsonb().fromJson(data, type);
    }
}
