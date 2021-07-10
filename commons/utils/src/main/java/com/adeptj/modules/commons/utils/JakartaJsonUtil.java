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

import javax.json.JsonBuilderFactory;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriterFactory;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.Map;

import static com.adeptj.modules.commons.utils.Constants.UTF8;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.json.stream.JsonGenerator.PRETTY_PRINTING;

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

    private static final JsonGeneratorFactory GENERATOR_FACTORY;

    private static final JsonParserFactory PARSER_FACTORY;

    private static final JsonBuilderFactory JSON_BUILDER_FACTORY;

    static {
        JsonProvider provider = JsonProvider.provider();
        JSONB = JsonbBuilder.newBuilder()
                .withProvider(provider)
                .withConfig(new JsonbConfig().withFormatting(FALSE).withEncoding(UTF8))
                .build();
        Map<String, ?> config = Map.of(PRETTY_PRINTING, TRUE);
        READER_FACTORY = provider.createReaderFactory(null);
        WRITER_FACTORY = provider.createWriterFactory(config);
        GENERATOR_FACTORY = provider.createGeneratorFactory(config);
        PARSER_FACTORY = provider.createParserFactory(null);
        JSON_BUILDER_FACTORY = provider.createBuilderFactory(null);
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

    public static JsonGeneratorFactory getJsonGeneratorFactory() {
        return GENERATOR_FACTORY;
    }

    public static JsonParserFactory getJsonParserFactory() {
        return PARSER_FACTORY;
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

    public static <T> T deserialize(byte[] data, Type type) {
        return JakartaJsonUtil.getJsonb().fromJson(new ByteArrayInputStream(data), type);
    }

    public static <T> T deserialize(String data, Class<T> type) {
        return JakartaJsonUtil.getJsonb().fromJson(data, type);
    }

    public static <T> T deserialize(String data, Type type) {
        return JakartaJsonUtil.getJsonb().fromJson(data, type);
    }
}
