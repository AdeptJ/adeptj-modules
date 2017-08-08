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
package com.adeptj.modules.jaxrs.core.jwt;

import com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.JaxRSAuthenticator;
import com.adeptj.modules.jaxrs.core.JaxRSException;
import com.adeptj.modules.jaxrs.core.RequiresJwt;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * JAX-RS resource for issuance and verification of JWT.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path("/auth")
@Component(immediate = true, service = JwtIssuer.class, property = JwtIssuer.RESOURCE_BASE)
public class JwtIssuer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtIssuer.class);

    private static final String BIND_JWT_SERVICE = "bindJwtService";

    private static final String UNBIND_JWT_SERVICE = "unbindJwtService";

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=authenticator";

    @Reference
    private JaxRSAuthenticator authenticator;

    // As per Felix SCR, dynamic references should be declared as volatile.
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            bind = BIND_JWT_SERVICE,
            unbind = UNBIND_JWT_SERVICE
    )
    private volatile JwtService jwtService;


    /**
     * Issue Jwt to the subject with given credentials.
     *
     * @param subject  the subject who needs the Jwt
     * @param password password associated with the subject
     * @return JAX-RS Response either having a Jwt or Http error 503
     */
    @POST
    @Path("/jwt/issue")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response issueJwt(@NotNull @FormParam("subject") String subject,
                             @NotNull @FormParam("password") String password) {
        Response response;
        if (this.jwtService == null) {
            LOGGER.warn("Can't issue token as JwtService unavailable!");
            response = Response.status(SERVICE_UNAVAILABLE).build();
        } else {
            try {
                JaxRSAuthenticationInfo authInfo = this.authenticator.handleSecurity(subject, password);
                if (authInfo == null) {
                    response = Response.status(UNAUTHORIZED).build();
                } else {
                    response = Response.ok()
                            .header(AUTHORIZATION, this.jwtService.issue(subject, authInfo))
                            .build();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw JaxRSException.builder()
                        .message(ex.getMessage())
                        .cause(ex)
                        .status(INTERNAL_SERVER_ERROR.getStatusCode())
                        .build();
            }
        }
        return response;
    }

    /**
     * This resource method exists to verify the Jwt issued earlier. Should not be called by clients directly.
     * <p>
     * Rather use the {@link RequiresJwt} annotation for automatic verification by {@link JwtFilter}
     *
     * @return response 200 if {@link JwtFilter} was able to verify the Jwt issued earlier.
     */
    @GET
    @Path("/jwt/verify")
    @RequiresJwt
    public Response verifyJwt() {
        return Response.ok("JWT is valid!!")
                .type(TEXT_PLAIN)
                .build();
    }

    // Lifecycle Methods

    protected void bindJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) {
        this.jwtService = null;
    }

}
