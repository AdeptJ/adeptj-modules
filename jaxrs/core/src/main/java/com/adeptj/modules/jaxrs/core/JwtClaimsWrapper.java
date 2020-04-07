package com.adeptj.modules.jaxrs.core;

import java.util.Map;

public class JwtClaimsWrapper {

    private final Map<String, Object> claims;

    private final boolean expired;

    public JwtClaimsWrapper(Map<String, Object> claims, boolean expired) {
        this.claims = claims;
        this.expired = expired;
    }

    public Object getClaim(String key) {
        return this.claims.get(key);
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public boolean isExpired() {
        return expired;
    }
}
