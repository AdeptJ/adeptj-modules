package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.JsonUtil;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * The Jwt deserializer based on Json-B.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtDeserializer implements Deserializer<Map<String, ?>> {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ?> deserialize(byte[] bytes) throws DeserializationException {
        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            return JsonUtil.jsonb().fromJson(stream, Map.class);
        } catch (IOException ex) {
            throw new DeserializationException(ex.getMessage(), ex);
        }
    }
}
