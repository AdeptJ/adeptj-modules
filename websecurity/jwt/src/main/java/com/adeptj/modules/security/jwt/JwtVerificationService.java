package com.adeptj.modules.security.jwt;

public interface JwtVerificationService {

    /**
     * Verify the passed jwt claim information using configured signing key.
     *
     * @param jwt claims information that has to be verified by the {@link io.jsonwebtoken.JwtParser}
     * @return the {@link JwtClaims} object containing the claims information.
     */
    JwtClaims verifyJwt(String jwt);
}
