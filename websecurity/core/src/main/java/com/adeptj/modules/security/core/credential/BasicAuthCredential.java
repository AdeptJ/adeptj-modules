package com.adeptj.modules.security.core.credential;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.adeptj.modules.security.core.SecurityConstants.AUTH_SCHEME_BASIC;
import static com.adeptj.modules.security.core.SecurityConstants.HEADER_AUTHORIZATION;

public class BasicAuthCredential implements Credential {

    private static final int TOKEN_START_POS = 6;

    private String credential;

    private BasicAuthCredential(String credential) {
        this.credential = credential;
    }

    public static Credential from(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.isNotEmpty(token) && StringUtils.startsWith(token, AUTH_SCHEME_BASIC)) {
            return new BasicAuthCredential(StringUtils.substring(token, TOKEN_START_POS));
        }
        return null;
    }

    public String getCredential() {
        return credential;
    }
}
