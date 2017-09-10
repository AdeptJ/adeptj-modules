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
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_BEARER;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.STATUS_SERVER_ERROR;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
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
@Component(immediate = true, service = JwtIssuer.class, property = JwtIssuer.RESOURCE_BASE)
public class JwtIssuer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtIssuer.class);

    private static final String BIND_JWT_SERVICE = "bindJwtService";

    private static final String UNBIND_JWT_SERVICE = "unbindJwtService";

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=authenticator";

    private JwtCookieConfig config;

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
        Response response;
        if (this.jwtService == null) {
            LOGGER.warn("Can't issue JWT as JwtService unavailable!");
            response = Response.status(SERVICE_UNAVAILABLE).build();
        } else {
            try {
                JaxRSAuthenticationInfo authInfo = this.authenticator.handleSecurity(username, password);
                response = authInfo == null ? Response.status(UNAUTHORIZED).build()
                        : this.createResponseWithJwt(username, authInfo);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw JaxRSException.builder()
                        .message(ex.getMessage())
                        .cause(ex)
                        .status(STATUS_SERVER_ERROR)
                        .logException(false)
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
        return Response.ok("JWT verified successfully!!")
                .type(TEXT_PLAIN)
                .build();
    }

    private Response createResponseWithJwt(String username, JaxRSAuthenticationInfo authInfo) {
        Response.ResponseBuilder builder = Response.status(NO_CONTENT);
        String jwt = this.jwtService.issueJwt(username, authInfo);
        if (this.config.enabled()) {
            builder.cookie(new NewCookie(this.config.name(), jwt,
                    this.config.path(),
                    this.config.domain(),
                    this.config.comment(),
                    this.config.maxAge(),
                    this.config.secure(),
                    this.config.httpOnly()));
        } else {
            builder.header(AUTHORIZATION, AUTH_SCHEME_BEARER + SPACE + jwt);
        }
        return builder.build();
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

    protected void unbindJwtService(JwtService jwtService) {
        this.jwtService = null;
    }

    @Activate
    protected void start(JwtCookieConfig config) {
        this.config = config;
        JwtCookieNameProvider.INSTANCE.setJwtCookieName(this.config.name());
    }
}
