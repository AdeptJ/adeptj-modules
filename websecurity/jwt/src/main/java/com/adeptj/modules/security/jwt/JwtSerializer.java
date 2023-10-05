package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.io.AbstractSerializer;

import java.io.OutputStream;
import java.util.Map;

/**
 * The Jwt serializer based on Json-B.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JwtSerializer extends AbstractSerializer<Map<String, ?>> {

    @Override
    protected void doSerialize(Map<String, ?> stringMap, OutputStream out) throws Exception {

    }
}
