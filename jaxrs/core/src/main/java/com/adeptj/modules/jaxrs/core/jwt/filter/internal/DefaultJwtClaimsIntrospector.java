package com.adeptj.modules.jaxrs.core.jwt.filter.internal;

import com.adeptj.modules.jaxrs.core.jwt.JwtClaimsIntrospector;

import java.util.Map;

public class DefaultJwtClaimsIntrospector implements JwtClaimsIntrospector {

    @Override
    public boolean introspect(Map<String, Object> claims) {
        return true;
    }
}
