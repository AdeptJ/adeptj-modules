package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.io.AbstractDeserializer;
import io.jsonwebtoken.io.DeserializationException;

import java.io.Reader;
import java.util.Map;

/**
 * The Jwt deserializer based on Json-B.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JwtDeserializer extends AbstractDeserializer<Map<String, ?>> {

    @Override
    public Map<String, ?> doDeserialize(Reader reader) throws DeserializationException {
        return null;
    }
}
