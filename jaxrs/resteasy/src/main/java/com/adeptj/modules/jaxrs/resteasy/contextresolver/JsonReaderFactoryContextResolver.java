package com.adeptj.modules.jaxrs.resteasy.contextresolver;

import javax.annotation.Priority;
import javax.json.JsonReaderFactory;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonReaderFactoryContextResolver.PRIORITY;

/**
 * ContextResolver for Jakarta's {@link JsonReaderFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Produces({"application/json", "application/*+json", "text/json"})
@Priority(PRIORITY)
@Provider
public class JsonReaderFactoryContextResolver implements ContextResolver<JsonReaderFactory> {

    static final int PRIORITY = 5500;

    @Override
    public JsonReaderFactory getContext(Class<?> type) {
        return type == JsonReaderFactory.class ? JakartaJsonProvider.getJsonReaderFactory() : null;
    }
}
