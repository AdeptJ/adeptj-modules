package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;

public class RsaVerificationKeyInfo {

    private final SignatureAlgorithm algorithm;

    private final String publicKey;

    public RsaVerificationKeyInfo(SignatureAlgorithm algorithm, String publicKey) {
        this.algorithm = algorithm;
        this.publicKey = publicKey;
    }

    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
