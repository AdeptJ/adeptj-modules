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

import com.adeptj.modules.security.jwt.ExtendedJwtClaims;
import com.adeptj.modules.security.jwt.validation.JwtClaimsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
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
public final class ClaimsJwsHandler extends JwtHandlerAdapter<ExtendedJwtClaims> {

    private static final String BIND_CLAIMS_VALIDATOR_SERVICE = "bindClaimsValidator";

    private static final String UNBIND_CLAIMS_VALIDATOR_SERVICE = "unbindClaimsValidator";

    private volatile boolean invokeClaimsValidator;

    // As per Felix SCR, dynamic references should be declared as volatile.
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            bind = BIND_CLAIMS_VALIDATOR_SERVICE,
            unbind = UNBIND_CLAIMS_VALIDATOR_SERVICE
    )
    private volatile JwtClaimsValidator claimsValidator;

    /**
     * Checks the signature algorithm first and then validates the {@link Claims} via {@link JwtClaimsValidator}.
     *
     * @param jws the Json web signature.
     * @return extended claims map with extra information such as roles etc. if any, to indicate the outcome
     * of {@link JwtClaimsValidator#validate} method.
     */
    @Override
    public ExtendedJwtClaims onClaimsJws(Jws<Claims> jws) {
        return new ExtendedJwtClaims()
                .addClaims(this.invokeClaimsValidator && this.claimsValidator != null ? this.claimsValidator.validate(jws.getBody())
                        : jws.getBody());
    }

    void setInvokeClaimsValidator(boolean invokeClaimsValidator) {
        this.invokeClaimsValidator = invokeClaimsValidator;
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
