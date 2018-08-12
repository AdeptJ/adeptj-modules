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
import com.adeptj.modules.security.jwt.validation.JwtClaimsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.Objects;

/**
 * Simple implementation of {@link JwtHandlerAdapter}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = ClaimsJwsHandler.class)
public final class ClaimsJwsHandler extends JwtHandlerAdapter<Boolean> {

    private static final String BIND_CLAIMS_VALIDATOR_SERVICE = "bindClaimsValidator";

    private static final String UNBIND_CLAIMS_VALIDATOR_SERVICE = "unbindClaimsValidator";

    private JwtConfig jwtConfig;

    // As per Felix SCR, dynamic references should be declared as volatile.
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            bind = BIND_CLAIMS_VALIDATOR_SERVICE,
            unbind = UNBIND_CLAIMS_VALIDATOR_SERVICE
    )
    private volatile JwtClaimsValidator claimsValidator;

    void setJwtConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * Checks the signature algorithm first and then validates the {@link Claims} via {@link JwtClaimsValidator}.
     *
     * @param jws the Json web signature.
     * @return boolean to indicate the outcome of {@link JwtClaimsValidator#validate} method.
     */
    @Override
    public Boolean onClaimsJws(Jws<Claims> jws) {
        if (!StringUtils.equals(this.jwtConfig.signatureAlgo(), jws.getHeader().getAlgorithm())) {
            throw new MalformedJwtException(String.format("SignatureAlgorithm must be [%s]!!",
                    this.jwtConfig.signatureAlgo()));
        }
        return !this.jwtConfig.invokeClaimsValidator()
                || this.claimsValidator != null && this.claimsValidator.validate(jws.getBody());
    }

    // ------------------------------------------------- OSGi INTERNAL -------------------------------------------------

    protected void bindClaimsValidator(JwtClaimsValidator claimsValidator) {
        this.claimsValidator = claimsValidator;
    }

    protected void unbindClaimsValidator(JwtClaimsValidator claimsValidator) { // NOSONAR
        if (Objects.equals(claimsValidator, this.claimsValidator)) {
            this.claimsValidator = null;
        }
    }
}
