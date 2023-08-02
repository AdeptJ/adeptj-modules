package com.adeptj.modules.security.oauth;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * This is meant to be implemented by the consumer of the oauth module, this will be invoked after getting the
 * access token using authorization code from the OAuth provider.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@ConsumerType
public interface OAuthAccessTokenConsumer {

    /**
     * Use the access token to get the profile information etc.
     *
     * @param token the access token from the OAuth provider.
     * @return a valid redirect location where the user should land on.
     */
    String consume(OAuthAccessToken token);
}
