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
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
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

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String ALGO_RSA = "RSA";

    private static final String CURR_DIR = "user.dir";

    private static final String DEFAULT_PEM = "/default.pem";

    private JwtConfig config;

    private String base64EncodedSigningKey;

    private SignatureAlgorithm signatureAlgo;

    private Key signingKey;

    @Override
    public String issueToken(String subject) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setSubject(subject)
                .setIssuer(this.config.issuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(LocalDateTime
                        .now()
                        .plusMinutes(this.config.expirationTime())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .setId(UUID.randomUUID().toString());
        this.signWith(jwtBuilder);
        return BEARER + jwtBuilder.compact();
    }

    private void signWith(JwtBuilder jwtBuilder) {
        if (StringUtils.isEmpty(this.base64EncodedSigningKey)) {
            jwtBuilder.signWith(this.signatureAlgo, this.signingKey);
        } else {
            jwtBuilder.signWith(this.signatureAlgo, this.base64EncodedSigningKey);
        }
    }

    @Override
    public boolean parseToken(String jwt) {
        boolean tokenParsed = false;
        try {
            JwtParser parser = Jwts.parser();
            this.setSigningKey(parser);
            Jws<Claims> claimsJws = parser.parseClaimsJws(jwt);
            LOGGER.info("Subject [{}] has a valid token!!", claimsJws.getBody().getSubject());
            tokenParsed = true;
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
            LOGGER.error("Invalid JWT!!", ex);
        }
        return tokenParsed;
    }

    private void setSigningKey(JwtParser parser) {
        if (StringUtils.isEmpty(this.base64EncodedSigningKey)) {
            parser.setSigningKey(this.signingKey);
        } else {
            parser.setSigningKey(this.base64EncodedSigningKey);
        }
    }

    // LifeCycle Methods

    @Activate
    protected void start(JwtConfig config) {
        this.config = config;
        try {
            this.signatureAlgo = SignatureAlgorithm.forName(config.signatureAlgo());
            String signKey = config.signingKey();
            if (StringUtils.isNotEmpty(signKey) && this.signatureAlgo.isHmac()) {
                this.base64EncodedSigningKey = new String(Base64.getEncoder().encode(signKey.getBytes(UTF8)));
            } else if (!this.signatureAlgo.isHmac()) {
                this.signingKey = this.resolveSigningKey();
            } else {
                // Default is RSA
                this.signingKey = this.resolveSigningKey();
            }
        } catch (Exception ex) { // NOSONAR
            // Let the exception be thrown so that SCR would not create a service object of this.
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    private Key resolveSigningKey() throws Exception {
        Key key = null;
        KeyFactory keyFactory = KeyFactory.getInstance(ALGO_RSA);
        // 1. try the config location
        String pemFileLocation = System.getProperty(CURR_DIR) + File.separator + this.config.pemFileLocation();
        if (StringUtils.isNotEmpty(pemFileLocation)) {
            try (FileInputStream inputStream = new FileInputStream(pemFileLocation)) {
                key = this.createKey(keyFactory, inputStream);
            } catch (FileNotFoundException ex) {
                //It's ok if pem file is not found. Will try the default.pem next.
            }
        }
        if (key == null) {
            try (InputStream inputStream = JwtServiceImpl.class.getResourceAsStream(DEFAULT_PEM)) {
                key = this.createKey(keyFactory, inputStream);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return key;
    }

    private Key createKey(KeyFactory keyFactory, InputStream inputStream) throws Exception {
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder()
                .decode(IOUtils.toString(inputStream, UTF8)
                        .replace(KEY_HEADER, EMPTY)
                        .replace(KEY_FOOTER, EMPTY)
                        .replaceAll(REGEX_SPACE, EMPTY))));
    }


}
