package com.adeptj.modules.jaxrs.resteasy.contextresolver;

import com.adeptj.modules.commons.utils.JakartaJsonUtil;

import jakarta.annotation.Priority;
import jakarta.json.JsonReaderFactory;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

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
        return type == JsonReaderFactory.class ? JakartaJsonUtil.getJsonReaderFactory() : null;
    }
}
