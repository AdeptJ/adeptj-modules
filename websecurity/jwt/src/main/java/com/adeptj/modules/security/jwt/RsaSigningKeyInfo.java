package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;

public class RsaSigningKeyInfo {

    private final SignatureAlgorithm algorithm;

    private final String privateKey;

    private String privateKeyPassword;

    public RsaSigningKeyInfo(SignatureAlgorithm algorithm, String privateKey) {
        this.algorithm = algorithm;
        this.privateKey = privateKey;
    }

    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }
}
