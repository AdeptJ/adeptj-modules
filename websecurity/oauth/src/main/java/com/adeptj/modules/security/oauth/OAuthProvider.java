package com.adeptj.modules.security.oauth;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;

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

    private OAuth20Service service;

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

    public OAuth20Service getService() {
        ServiceBuilder builder = new ServiceBuilder(this.apiKey)
                .apiSecret(this.apiSecret)
                .callback(this.callback + "/" + this.providerType.toString());
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

        public Builder(String providerName) {
            this.providerType = OAuthProviderType.from(providerName);
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

        public OAuthProvider build() {
            return new OAuthProvider(this.providerType, this.apiKey, this.apiSecret, this.callback);
        }
    }

    @Override
    public String toString() {
        return "Builder [providerName=" + this.providerType.toString() + ", callback=" + this.callback + "]";
    }
}
