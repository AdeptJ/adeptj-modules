package com.adeptj.modules.security.oauth;

/**
 * General purpose exception thrown by {@link OAuthProvider}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OAuthProviderException extends RuntimeException {

    public OAuthProviderException(String message) {
        super(message);
    }

    public OAuthProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
