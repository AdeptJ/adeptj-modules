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

import com.adeptj.modules.jaxrs.core.JaxRSException;
import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.auth.spi.JaxRSAuthenticator;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_BEARER;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.STATUS_SERVER_ERROR;
import static com.adeptj.modules.jaxrs.core.jwt.JwtResource.RESOURCE_BASE;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.apache.commons.lang3.StringUtils.SPACE;

/**
 * JAX-RS resource for issuance and verification of JWT.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path("/auth")
@Designate(ocd = JwtCookieConfig.class)
@Component(immediate = true, service = JwtResource.class, property = RESOURCE_BASE)
public class JwtResource {

    private static final String BIND_JWT_SERVICE = "bindJwtService";

    private static final String UNBIND_JWT_SERVICE = "unbindJwtService";

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=authenticator";

    private JwtCookieConfig cookieConfig;

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
     * Issue Jwt to the username with given credentials.
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     * @return JAX-RS Response either having a Jwt or Http error 503
     */
    @POST
    @Path("/jwt/issue")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response issueJwt(@NotNull @FormParam("username") String username,
                             @NotNull @FormParam("password") String password) {
        if (this.jwtService == null) {
            return Response.status(SERVICE_UNAVAILABLE).build();
        }
        try {
            JaxRSAuthenticationInfo authInfo = this.authenticator.handleSecurity(username, password);
            return authInfo == null ? Response.status(UNAUTHORIZED).build() :
                    this.responseWithJwt(username, authInfo);
        } catch (Exception ex) {
            throw JaxRSException.builder()
                    .message(ex.getMessage())
                    .cause(ex)
                    .status(STATUS_SERVER_ERROR)
                    .logException(true)
                    .build();
        }
    }

    /**
     * This resource method exists to verify the Jwt issued earlier. Should not be called by clients directly.
     * <p>
     * Rather use the {@link RequiresJwt} annotation for automatic verification by {@link JwtFilter}
     *
     * @return response 200 if {@link JwtFilter} was able to verify the Jwt issued earlier.
     */
    @RequiresJwt
    @GET
    @Path("/jwt/verify")
    public Response verifyJwt() {
        return Response.ok("JWT verified successfully!!")
                .type(TEXT_PLAIN)
                .build();
    }

    private Response responseWithJwt(String username, JaxRSAuthenticationInfo authInfo) {
        String jwt = this.jwtService.issueJwt(username, authInfo);
        return this.cookieConfig.enabled() ?
                Response.ok().cookie(JwtUtil.buildJwtCookie(this.cookieConfig, jwt)).build() :
                Response.ok().header(AUTHORIZATION, AUTH_SCHEME_BEARER + SPACE + jwt).build();
    }

    /**
     * Provides the JWT cookie name configured by this component.
     */
    enum JwtCookieNameProvider {

        INSTANCE;

        private String jwtCookieName;

        String getJwtCookieName() {
            return jwtCookieName;
        }

        void setJwtCookieName(String jwtCookieName) {
            this.jwtCookieName = jwtCookieName;
        }
    }

    // Component Lifecycle Methods

    protected void bindJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) { // NOSONAR
        this.jwtService = null;
    }

    @Activate
    protected void start(JwtCookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
        JwtCookieNameProvider.INSTANCE.setJwtCookieName(this.cookieConfig.name());
    }
}
