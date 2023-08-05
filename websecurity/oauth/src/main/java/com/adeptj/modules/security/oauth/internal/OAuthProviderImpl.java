package com.adeptj.modules.security.oauth.internal;

import com.adeptj.modules.security.oauth.OAuthAccessToken;
import com.adeptj.modules.security.oauth.OAuthProvider;
import com.adeptj.modules.security.oauth.OAuthProviderException;
import com.adeptj.modules.security.oauth.OAuthProviderType;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation for OAuthProvider
 *
 * @author Rakesh Kumar, AdeptJ
 */
public class OAuthProviderImpl implements OAuthProvider {

    private final OAuthProviderType providerType;

    private final String clientId;

    private final String clientSecret;

    private final String redirectUri;

    private String scope;

    private boolean debug;

    public OAuthProviderImpl(String providerName, String clientId, String clientSecret, String redirectUri) {
        this.providerType = OAuthProviderType.from(providerName);
        Validate.isTrue((this.providerType != null), String.format("Unknown provider(%s)", providerName));
        Validate.isTrue(StringUtils.isNotEmpty(clientId), "clientId can't be null!");
        Validate.isTrue(StringUtils.isNotEmpty(clientSecret), "clientSecret can't be null!");
        Validate.isTrue(StringUtils.isNotEmpty(redirectUri), "redirectUri can't be null!");
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri + "/" + this.providerType;
    }

    @Override
    public OAuthProviderType getProviderType() {
        return this.providerType;
    }

    @Override
    public String getAuthorizationUrl() {
        try (OAuth20Service service = this.getService()) {
            return service.getAuthorizationUrl();
        } catch (IOException ex) {
            throw new OAuthProviderException(ex.getMessage(), ex);
        }
    }

    @Override
    public OAuthAccessToken getAccessToken(String code) {
        try (OAuth20Service service = this.getService()) {
            OAuth2AccessToken token = service.getAccessToken(code);
            return new OAuthAccessToken(token.getAccessToken(), token.getTokenType(),
                    token.getExpiresIn(),
                    token.getRefreshToken(),
                    token.getScope());
        } catch (Exception ex) { // NOSONAR
            throw new OAuthProviderException(ex.getMessage(), ex);
        }
    }

    @Override
    public void doWithOAuthService(Consumer<OAuth20Service> consumer) {
        try (OAuth20Service service = this.getService()) {
            consumer.accept(service);
        } catch (Exception ex) { // NOSONAR
            throw new OAuthProviderException(ex.getMessage(), ex);
        }
    }

    @Override
    public <T> T doWithOAuthService(Function<OAuth20Service, T> function) {
        try (OAuth20Service service = this.getService()) {
            return function.apply(service);
        } catch (Exception ex) { // NOSONAR
            throw new OAuthProviderException(ex.getMessage(), ex);
        }
    }

    void setScope(String[] scopes) {
        if (scopes != null && scopes.length > 0) {
            this.scope = String.join(" ", scopes);
        }
    }

    void setDebug(boolean debug) {
        this.debug = debug;
    }

    private OAuth20Service getService() {
        ServiceBuilder builder = new ServiceBuilder(this.clientId)
                .apiSecret(this.clientSecret)
                .callback(this.redirectUri);
        if (StringUtils.isNotEmpty(this.scope)) {
            builder.defaultScope(this.scope);
        }
        if (this.debug) {
            builder.debug();
        }
        return builder.build(this.getApi());
    }

    private DefaultApi20 getApi() {
        return switch (this.providerType) {
            case GOOGLE -> GoogleApi20.instance();
            case FACEBOOK -> FacebookApi.instance();
            case GITHUB -> GitHubApi.instance();
            case LINKEDIN -> LinkedInApi20.instance();
        };
    }

    @Override
    public String toString() {
        return "OAuthProvider [name=" + this.providerType + ", redirectUri=" + this.redirectUri + "]";
    }
}
