package com.adeptj.modules.security.oauth;

/**
 * Pojo for holding access token info.
 *
 * @author Rakesh Kumar, AdeptJ
 */
public class OAuthAccessToken {

    private final String accessToken;

    private final String tokenType;

    private final Integer expiresIn;

    private final String refreshToken;

    private final String scope;

    public OAuthAccessToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }
}
