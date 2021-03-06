package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.JakartaJsonUtil;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;

import javax.json.bind.JsonbException;
import java.util.Map;

/**
 * The Jwt deserializer based on Json-B.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtDeserializer implements Deserializer<Map<String, ?>> {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ?> deserialize(byte[] bytes) {
        try {
            return JakartaJsonUtil.deserialize(bytes, Map.class);
        } catch (JsonbException ex) {
            throw new DeserializationException(ex.getMessage(), ex);
        }
    }
}
