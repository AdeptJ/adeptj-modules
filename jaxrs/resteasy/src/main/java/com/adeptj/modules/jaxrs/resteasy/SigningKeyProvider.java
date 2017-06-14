package com.adeptj.modules.jaxrs.resteasy;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * JWT SigningKeyProvider.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public enum SigningKeyProvider {

    INSTANCE;

    private Key signingKey;

    SigningKeyProvider() {
        String keyString = "V3ryS3cr3t";
        this.signingKey =  new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "SHA-256");
    }

    public Key signingKey() {
        return this.signingKey;
    }
}
