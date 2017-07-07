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

package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.security.jwt.JwtConfig;
import com.adeptj.modules.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service for signing and parsing JWT.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JwtConfig.class)
@Component(configurationPolicy = REQUIRE)
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceImpl.class);

    private static final String BEARER = "Bearer ";

    private static final String UTF8 = "UTF-8";

    private JwtConfig config;

    private String base64EncodedSigningKey;

    @Override
    public String issueToken(String subject) {
        return BEARER + Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setSubject(subject)
                .setIssuer(this.config.issuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(LocalDateTime
                        .now()
                        .plusMinutes(this.config.expirationTime())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .signWith(SignatureAlgorithm.forName(this.config.signatureAlgo()), this.base64EncodedSigningKey)
                .setId(UUID.randomUUID().toString())
                .compact();
    }

    @Override
    public boolean parseToken(String jwt) {
        boolean tokenParsed = false;
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(this.base64EncodedSigningKey)
                    .parseClaimsJws(jwt);
            LOGGER.info("Subject [{}] has a valid token!!", claimsJws.getBody().getSubject());
            tokenParsed = true;
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
            LOGGER.error("Invalid JWT!!", ex);
        }
        return tokenParsed;
    }

    // LifeCycle Methods

    @Activate
    protected void start(JwtConfig config) {
        this.config = config;
        try {
            this.base64EncodedSigningKey = new String(Base64.getEncoder().encode(config.signingKey().getBytes(UTF8)));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    @Deactivate
    protected void stop() {
    }
}
