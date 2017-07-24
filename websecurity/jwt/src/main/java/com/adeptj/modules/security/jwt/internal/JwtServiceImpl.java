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
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service for signing and parsing JWT
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JwtConfig.class)
@Component(configurationPolicy = REQUIRE)
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceImpl.class);

    private static final String BEARER_SCHEMA = "Bearer";

    private static final String UTF8 = "UTF-8";

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String ALGO_RSA = "RSA";

    private static final String CURR_DIR = "user.dir";

    private static final String DEFAULT_KEY_FILE = "/default.pem";

    private static final String KEY_NULL_MSG = "Key must not be null!!";

    private JwtConfig jwtConfig;

    private String base64EncodedSigningKey;

    private SignatureAlgorithm signatureAlgo;

    private Key signingKey;

    /**
     * {@inheritDoc}
     */
    @Override
    public String issueJwt(String subject, Map<String, Object> payload) {
        // Lets first set the claims, we don't want callers to act smart and pass the default claims parameters
        // such as "iss", "sub", "iat" etc. Since its a map and existing keys will be replaced with the new ones
        // provided in the payload which is not the intended behaviour. Default claims parameters should come from
        // JwtConfig and others can be generated at execution time such as "exp" etc.
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(payload)
                .setHeaderParam(TYPE, JWT_TYPE)
                .setSubject(subject)
                .setIssuer(this.jwtConfig.issuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(LocalDateTime.now()
                        .plusMinutes(this.jwtConfig.expirationTime())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .setId(UUID.randomUUID().toString());
        this.sign(jwtBuilder);
        return BEARER_SCHEMA + SPACE + jwtBuilder.compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parseClaimsJws(String claimsJws) {
        boolean claimsJwsParsed = false;
        try {
            JwtParser jwtParser = Jwts.parser();
            this.setSigningKey(jwtParser);
            Jws<Claims> jws = jwtParser.parseClaimsJws(claimsJws);
            LOGGER.info("Subject [{}] has a valid token!!", jws.getBody().getSubject());
            claimsJwsParsed = true;
        } catch (RuntimeException ex) {
            LOGGER.error("Invalid JWT!!", ex);
        }
        return claimsJwsParsed;
    }

    // LifeCycle Methods

    @Activate
    protected void start(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        try {
            this.signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
            String signKey = jwtConfig.signingKey();
            if (StringUtils.isNotEmpty(signKey) && this.signatureAlgo.isHmac()) {
                this.base64EncodedSigningKey = new String(Base64.getEncoder().encode(signKey.getBytes(UTF8)));
            } else if (StringUtils.isEmpty(signKey) && this.signatureAlgo.isHmac()) {
                this.signatureAlgo = SignatureAlgorithm.RS256;
                this.signingKey = Objects.requireNonNull(this.resolveSigningKey(), KEY_NULL_MSG);
            } else {
                // Default is RS256
                if (this.signatureAlgo.isHmac()) {
                    this.signatureAlgo = SignatureAlgorithm.RS256;
                }
                this.signingKey = Objects.requireNonNull(this.resolveSigningKey(), KEY_NULL_MSG);
            }
        } catch (Exception ex) { // NOSONAR
            // Let the exception be thrown so that SCR would not create a service object of this component.
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    private Key resolveSigningKey() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGO_RSA);
        // 1. try the jwtConfig location
        String keyFileLocation = System.getProperty(CURR_DIR) + File.separator + this.jwtConfig.keyFileLocation();
        LOGGER.info("Loading Key file from location: [{}]", keyFileLocation);
        Key key = this.loadFromLocation(keyFactory, keyFileLocation);
        if (key == null) {
            LOGGER.warn("Couldn't load Key file from location [{}], using the default one!!", keyFileLocation);
            // 2. Use the default one that is embedded with this module.
            key = this.loadDefault(keyFactory);
        }
        return key;
    }

    private Key loadDefault(KeyFactory keyFactory) {
        Key key = null;
        try (InputStream inputStream = JwtServiceImpl.class.getResourceAsStream(DEFAULT_KEY_FILE)) {
            key = this.generatePrivateKey(keyFactory, inputStream);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return key;
    }

    private Key loadFromLocation(KeyFactory keyFactory, String keyFileLocation) throws Exception {
        Key key = null;
        try (FileInputStream inputStream = new FileInputStream(keyFileLocation)) {
            key = this.generatePrivateKey(keyFactory, inputStream);
        }
        return key;
    }

    private Key generatePrivateKey(KeyFactory keyFactory, InputStream inputStream) throws Exception {
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder()
                .decode(IOUtils.toString(inputStream, UTF8)
                        .replace(KEY_HEADER, EMPTY)
                        .replace(KEY_FOOTER, EMPTY)
                        .replaceAll(REGEX_SPACE, EMPTY))));
    }

    private void sign(JwtBuilder jwtBuilder) {
        if (this.signingKey == null) {
            jwtBuilder.signWith(this.signatureAlgo, this.base64EncodedSigningKey);
        } else {
            jwtBuilder.signWith(this.signatureAlgo, this.signingKey);
        }
    }

    private void setSigningKey(JwtParser parser) {
        if (this.signingKey == null) {
            parser.setSigningKey(this.base64EncodedSigningKey);
        } else {
            parser.setSigningKey(this.signingKey);
        }
    }
}
