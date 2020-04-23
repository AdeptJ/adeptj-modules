package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.JsonUtil;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * The Jwt deserializer based on Jackson.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtDeserializer implements Deserializer<Map<String, ?>> {

    @Override
    public Map<String, ?> deserialize(byte[] bytes) throws DeserializationException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            return JsonUtil.jsonb().fromJson(bis, (Type) Map.class);
        } catch (IOException ex) {
            throw new DeserializationException(ex.getMessage(), ex);
        }
    }
}
