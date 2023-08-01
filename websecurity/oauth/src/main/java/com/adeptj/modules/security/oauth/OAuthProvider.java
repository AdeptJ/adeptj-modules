package com.adeptj.modules.security.oauth;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * OAuthProvider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OAuthProvider {

    private final OAuthProviderType providerType;

    private final String apiKey;

    private final String apiSecret;

    private final String callback;

    private String scope;

    private OAuthProvider(OAuthProviderType providerType, String apiKey, String apiSecret, String callback) {
        this.providerType = providerType;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.callback = callback;
    }

    public OAuthProviderType getProviderType() {
        return providerType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getCallback() {
        return callback;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public OAuth20Service getService() {
        ServiceBuilder builder = new ServiceBuilder(this.apiKey)
                .apiSecret(this.apiSecret)
                .callback(this.callback + "/" + this.providerType.toString());
        if (StringUtils.isNotEmpty(this.scope)) {
            builder.withScope(this.scope);
        }
        DefaultApi20 api = null;
        switch (this.providerType) {
            case GITHUB:
                api = GitHubApi.instance();
                break;
            case GOOGLE:
                api = GoogleApi20.instance();
                break;
            case FACEBOOK:
                api = FacebookApi.instance();
                break;
            case LINKEDIN:
                api = LinkedInApi20.instance();
        }
        return builder.build(api);
    }

    public static Builder builder(String providerName) {
        return new Builder(providerName);
    }

    /**
     * OAuthProvider.Builder
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    public static class Builder {

        private final OAuthProviderType providerType;

        private String apiKey;

        private String apiSecret;

        private String callback;

        private Set<String> scopes;

        public Builder(String providerName) {
            this.providerType = OAuthProviderType.from(providerName);
            Validate.isTrue((this.providerType != null), String.format("Unknown provider(%s)", providerName));
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder callback(String callback) {
            this.callback = callback;
            return this;
        }

        public Builder scopes(String... scopes) {
            if (scopes != null && scopes.length > 0) {
                if (this.scopes == null) {
                    this.scopes = new HashSet<>();
                }
                Collections.addAll(this.scopes, scopes);
            }
            return this;
        }

        public OAuthProvider build() {
            OAuthProvider provider = new OAuthProvider(this.providerType, this.apiKey, this.apiSecret, this.callback);
            if (this.scopes != null && !this.scopes.isEmpty()) {
                provider.setScope(String.join(" ", this.scopes));
            }
            return provider;
        }
    }

    @Override
    public String toString() {
        return "Builder [providerName=" + this.providerType.toString() + ", callback=" + this.callback + "]";
    }
}
