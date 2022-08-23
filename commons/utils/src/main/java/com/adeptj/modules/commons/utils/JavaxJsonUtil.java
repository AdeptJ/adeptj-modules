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
 * Provides Jakarta(javax namespace) {@link Jsonb} and other objects from Jakarta Json-P.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JavaxJsonUtil {

    private JavaxJsonUtil() {
    }

    private static final Jsonb JSONB;

    private static final JsonReaderFactory READER_FACTORY;

    private static final JsonWriterFactory WRITER_FACTORY;

    private static final JsonGeneratorFactory GENERATOR_FACTORY;

    private static final JsonParserFactory PARSER_FACTORY;

    private static final JsonBuilderFactory JSON_BUILDER_FACTORY;

    static {
        JsonProvider provider = JsonProvider.provider();
        Map<String, ?> config = Map.of(PRETTY_PRINTING, TRUE);
        READER_FACTORY = provider.createReaderFactory(null);
        WRITER_FACTORY = provider.createWriterFactory(config);
        GENERATOR_FACTORY = provider.createGeneratorFactory(config);
        PARSER_FACTORY = provider.createParserFactory(null);
        JSON_BUILDER_FACTORY = provider.createBuilderFactory(null);
        JSONB = JsonbBuilder.newBuilder()
                .withProvider(provider)
                .withConfig(new JsonbConfig().withFormatting(FALSE).withEncoding(UTF8))
                .build();
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

    // Serialize methods.

    public static <T> String serialize(T object) {
        return JavaxJsonUtil.getJsonb().toJson(object);
    }

    public static <T> byte[] serializeToBytes(T object) {
        return JavaxJsonUtil.serialize(object).getBytes(UTF_8);
    }

    // Deserialize methods.

    public static <T> T deserialize(byte[] data, Class<T> type) {
        return JavaxJsonUtil.getJsonb().fromJson(new ByteArrayInputStream(data), type);
    }

    public static <T> T deserialize(String data, Class<T> type) {
        return JavaxJsonUtil.getJsonb().fromJson(data, type);
    }

    public static <T> T deserialize(byte[] data, Type type) {
        return JavaxJsonUtil.getJsonb().fromJson(new ByteArrayInputStream(data), type);
    }

    public static <T> T deserialize(String data, Type type) {
        return JavaxJsonUtil.getJsonb().fromJson(data, type);
    }
}
