package com.adeptj.modules.security.oauth.jaxrs;

import com.adeptj.modules.security.oauth.OAuthProviderService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class BaseOAuthResource {

    OAuthProviderService resolveOAuthProviderService(String provider, List<OAuthProviderService> providers) {
        return providers.stream()
                .filter(ps -> StringUtils.equals(ps.getOAuthProvider().getProviderType().toString(), provider))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unknown provider(%s)", provider)));
    }
}
