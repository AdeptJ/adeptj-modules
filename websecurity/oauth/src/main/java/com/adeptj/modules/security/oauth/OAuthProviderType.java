package com.adeptj.modules.security.oauth;

import org.apache.commons.lang3.StringUtils;

/**
 * Provider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public enum OAuthProviderType {

    GITHUB("github"),

    GOOGLE("google"),

    FACEBOOK("facebook"),

    LINKEDIN("linkedin");

    private final String providerName;

    OAuthProviderType(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public String toString() {
        return this.providerName;
    }

    public static OAuthProviderType from(String providerName) {
        for (OAuthProviderType providerType : OAuthProviderType.values()) {
            if (StringUtils.equals(providerName, providerType.toString())) {
                return providerType;
            }
        }
        return null;
    }
}
