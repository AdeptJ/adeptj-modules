package com.adeptj.modules.security.oauth;

import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * OAuthProvider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface OAuthProvider {

    OAuthProviderType getProviderType();

    String getAuthorizationUrl();

    OAuthAccessToken getAccessToken(String code);

    void doWithOAuthService(Consumer<OAuth20Service> consumer);

    <T> T doWithOAuthService(Function<OAuth20Service, T> function);
}
