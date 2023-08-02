package com.adeptj.modules.security.oauth;

/**
 * Exception for scenario when an unknown provider is asked to initiate the OAuth request.
 *
 * @author Rakesh Kumar, AdeptJ
 */
public class NoSuchOAuthProviderException extends OAuthProviderException {

    public NoSuchOAuthProviderException(String message) {
        super(message);
    }
}
