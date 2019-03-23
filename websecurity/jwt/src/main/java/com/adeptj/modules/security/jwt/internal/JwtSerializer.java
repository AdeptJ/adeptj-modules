package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.Jackson;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.lang.Assert;

/**
 * The Jwt serializer based on Jackson.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(T t) throws SerializationException {
        Assert.notNull(t, "Object to serialize cannot be null.");
        try {
            return Jackson.objectWriter().writeValueAsBytes(t);
        } catch (JsonProcessingException ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }
}
