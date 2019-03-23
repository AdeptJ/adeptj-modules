package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.Jackson;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;

import java.io.IOException;

/**
 * The Jwt deserializer based on Jackson.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtDeserializer<T> implements Deserializer<T> {

    @Override
    public T deserialize(byte[] bytes) throws DeserializationException {
        try {
            return Jackson.objectReader().forType(Object.class).readValue(bytes);
        } catch (IOException ex) {
            throw new DeserializationException(ex.getMessage(), ex);
        }
    }
}
