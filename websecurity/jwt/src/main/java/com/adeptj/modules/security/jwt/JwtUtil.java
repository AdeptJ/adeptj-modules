/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.lang.Assert;

import java.util.Map;
import java.util.stream.Stream;

import static com.adeptj.modules.security.jwt.JwtClaims.KEY_JWT_EXPIRED;

/**
 * JWT utilities.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JwtUtil {

    private static final String CLAIM_NOT_FOUND_MSG = "JWT claim [%s] not found in claims map!!";

    /**
     * Utility methods only.
     */
    private JwtUtil() {
    }

    /**
     * Validates the claim information passed.
     *
     * @param claims Caller supplied JWT claims map
     * @since 1.1.0.Final
     */
    public static void assertClaims(Map<String, Object> claims) {
        Assert.notEmpty(claims, "Claims map can't be null or empty!!");
        claims.forEach((claim, value) -> {
            if (value instanceof String) {
                Assert.hasText((String) value, String.format("%s can't be blank!!", claim));
            } else {
                Assert.notNull(value, String.format("%s can't be null!!", claim));
            }
        });
    }

    /**
     * Validates the claim information passed.
     *
     * @param claims          Caller supplied JWT claims map
     * @param mandatoryClaims obligatory claims that should be present in claims map.
     * @since 1.1.0.Final
     */
    public static void assertClaims(Map<String, Object> claims, String[] mandatoryClaims) {
        assertClaims(claims);
        Stream.of(mandatoryClaims)
                .forEach(claim -> Assert.isTrue(claims.containsKey(claim), String.format(CLAIM_NOT_FOUND_MSG, claim)));
    }

    /**
     * Checks whether the claims contains the key {@link JwtClaims#KEY_JWT_EXPIRED}.
     *
     * @param claims the Jwt claims
     * @return a boolean to indicate whether the claims contains the key {@link JwtClaims#KEY_JWT_EXPIRED}.
     */
    public static boolean isExpired(Map<String, Object> claims) {
        return Boolean.parseBoolean((String) claims.get(KEY_JWT_EXPIRED));
    }
}
