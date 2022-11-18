package com.adeptj.modules.jaxrs.resteasy.contextresolver;

import com.adeptj.modules.commons.utils.JakartaJsonUtil;

import jakarta.annotation.Priority;
import jakarta.json.JsonWriterFactory;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

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
        return type == JsonWriterFactory.class ? JakartaJsonUtil.getJsonWriterFactory() : null;
    }
}
