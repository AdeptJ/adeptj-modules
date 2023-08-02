package com.adeptj.modules.security.oauth;

import org.jetbrains.annotations.NotNull;

/**
 * OAuthProviderService.
 *
 * @author Rakesh Kumar, AdeptJ
 */
public interface OAuthProviderService {

    @NotNull
    OAuthProvider getProvider(String providerName);
}
