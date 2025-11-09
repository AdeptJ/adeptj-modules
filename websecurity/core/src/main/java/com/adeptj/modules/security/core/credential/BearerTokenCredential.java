package com.adeptj.modules.security.core.credential;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.adeptj.modules.security.core.SecurityConstants.AUTH_SCHEME_BEARER;
import static com.adeptj.modules.security.core.SecurityConstants.HEADER_AUTHORIZATION;

public class BearerTokenCredential implements Credential {

    private static final int TOKEN_START_POS = 7;

    private final String token;

    private BearerTokenCredential(String token) {
        this.token = token;
    }

    public static @Nullable Credential from(@NotNull HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.isNotEmpty(token) && Strings.CS.startsWith(token, AUTH_SCHEME_BEARER)) {
            return new BearerTokenCredential(StringUtils.substring(token, TOKEN_START_POS));
        }
        return null;
    }

    public String getToken() {
        return token;
    }
}
