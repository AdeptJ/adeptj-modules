/*
###############################################################################
#                                                                             #
#    Copyright 2016-2022, AdeptJ (http://www.adeptj.com)                      #
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

import com.adeptj.modules.security.jwt.ExternalJwsAlgorithm;
import com.adeptj.modules.security.jwt.ExternalJwtVerificationService;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtKeyInitializationException;
import com.adeptj.modules.security.jwt.JwtKeys;
import com.adeptj.modules.security.jwt.JwtVerifier;
import com.adeptj.modules.security.jwt.RsaVerificationKeyInfo;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.Key;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Rsa based implementation of {@link ExternalJwtVerificationService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = RsaExternalJwtConfig.class)
@ExternalJwsAlgorithm("Rsa")
@Component(service = ExternalJwtVerificationService.class, configurationPolicy = REQUIRE)
public class RsaExternalJwtVerificationService implements ExternalJwtVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final JwtVerifier jwtVerifier;

    @Activate
    public RsaExternalJwtVerificationService(@NotNull RsaExternalJwtConfig config) {
        try {
            SignatureAlgorithm algorithm = SignatureAlgorithm.forName(config.signature_algorithm());
            LOGGER.info("Selected JWT SignatureAlgorithm: [{}]", algorithm.getJcaName());
            Key verificationKey = JwtKeys.createVerificationKey(new RsaVerificationKeyInfo(algorithm, config.public_key()));
            this.jwtVerifier = new JwtVerifier(verificationKey, config.log_jwt_verification_exception_trace());
        } catch (SignatureException | JwtKeyInitializationException | IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public JwtClaims verifyJwt(String jwt) {
        return this.jwtVerifier.verify(jwt);
    }
}
