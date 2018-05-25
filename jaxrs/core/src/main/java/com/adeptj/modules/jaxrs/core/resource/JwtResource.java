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
package com.adeptj.modules.jaxrs.core.resource;

import com.adeptj.modules.jaxrs.core.JaxRSResponses;
import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.auth.spi.JaxRSAuthenticator;
import com.adeptj.modules.jaxrs.core.jwt.JwtCookieConfig;
import com.adeptj.modules.jaxrs.core.jwt.JwtCookieConfigHolder;
import com.adeptj.modules.jaxrs.core.jwt.JwtUtil;
import com.adeptj.modules.jaxrs.core.jwt.RequiresJwt;
import com.adeptj.modules.jaxrs.core.jwt.filter.internal.StaticJwtFilter;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.metatype.annotations.Designate;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter.BIND_JWT_SERVICE;
import static com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter.UNBIND_JWT_SERVICE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * JAX-RS resource for issuance and verification of JWT.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxrsResource
@Path("/auth")
@Designate(ocd = JwtCookieConfig.class)
@Component(immediate = true, service = JwtResource.class)
public class JwtResource {

    @Reference
    private JaxRSAuthenticator authenticator;

    /**
     * The {@link JwtService} is optionally referenced.
     * If unavailable this resource will set a Service Unavailable (503) status.
     * <p>
     * Note: As per Felix SCR, dynamic references should be declared as volatile.
     */
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            bind = BIND_JWT_SERVICE,
            unbind = UNBIND_JWT_SERVICE
    )
    private volatile JwtService jwtService;


    /**
     * Create Jwt for the username with given credentials.
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     * @return JAX-RS Response either having a Jwt or Http error 503
     */
    @POST
    @Path("/jwt/create")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response createJwt(@NotEmpty @FormParam("username") String username,
                              @NotEmpty @FormParam("password") String password) {
        if (this.jwtService == null) {
            return JaxRSResponses.unavailable();
        }
        Optional<JaxRSAuthenticationInfo> optionalAuthInfo = this.authenticator.handleSecurity(username, password);
        return optionalAuthInfo.isPresent()
                ? JwtUtil.responseWithJwt(this.jwtService.createJwt(username, optionalAuthInfo.get()))
                : JaxRSResponses.unauthorized();
    }

    /**
     * This resource method exists to verify the Jwt issued earlier. Should not be called by clients directly.
     * <p>
     * Rather use the {@link RequiresJwt} annotation for automatic verification by {@link StaticJwtFilter}
     *
     * @return response 200 if {@link StaticJwtFilter} was able to verify the Jwt issued earlier.
     */
    @RequiresJwt
    @GET
    @Path("/jwt/verify")
    public Response verifyJwt() {
        return JaxRSResponses.ok("Jwt verified successfully!!", TEXT_PLAIN);
    }

    // -------------------- INTERNAL --------------------

    // Component Lifecycle Methods

    protected void bindJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) { // NOSONAR
        this.jwtService = null;
    }

    @Activate
    protected void start(JwtCookieConfig cookieConfig) {
        JwtCookieConfigHolder.getInstance().setJwtCookieConfig(cookieConfig);
    }

    @Modified
    protected void updated(JwtCookieConfig cookieConfig) {
        JwtCookieConfigHolder.getInstance().setJwtCookieConfig(cookieConfig);
    }
}
