package com.adeptj.modules.jaxrs.core;

import javax.json.JsonBuilderFactory;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriterFactory;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Provides Jakarta(javax namespace) {@link Jsonb} and other objects from Jakarta Json-P.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JavaxJsonProvider {

    private JavaxJsonProvider() {
    }

    private static final String UTF8 = "UTF-8";

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
        Map<String, ?> config = Map.of(JsonGenerator.PRETTY_PRINTING, TRUE);
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
}
