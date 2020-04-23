package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.JsonUtil;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.lang.Assert;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The Jwt serializer based on Jackson.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtSerializer implements Serializer<Map<String, ?>> {

    @Override
    public byte[] serialize(Map<String, ?> claims) throws SerializationException {
        Assert.notNull(claims, "Claims map to serialize cannot be null.");
        try {
            return JsonUtil.jsonb().toJson(claims).getBytes(UTF_8);
        } catch (Exception ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }
}
