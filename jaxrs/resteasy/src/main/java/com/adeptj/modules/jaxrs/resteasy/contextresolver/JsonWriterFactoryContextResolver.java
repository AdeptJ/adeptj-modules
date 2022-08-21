package com.adeptj.modules.jaxrs.resteasy.contextresolver;

import javax.annotation.Priority;
import javax.json.JsonWriterFactory;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonWriterFactoryContextResolver.PRIORITY;

/**
 * ContextResolver for Jakarta's {@link JsonWriterFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Produces({"application/json", "application/*+json", "text/json"})
@Priority(PRIORITY)
@Provider
public class JsonWriterFactoryContextResolver implements ContextResolver<JsonWriterFactory> {

    static final int PRIORITY = 6000;

    @Override
    public JsonWriterFactory getContext(Class<?> type) {
        return type == JsonWriterFactory.class ? JakartaJsonProvider.getJsonWriterFactory() : null;
    }
}
