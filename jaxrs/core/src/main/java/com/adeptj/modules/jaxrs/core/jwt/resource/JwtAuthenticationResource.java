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

package com.adeptj.modules.jaxrs.core.jwt.resource;

import com.adeptj.modules.jaxrs.api.JaxRSAuthenticationOutcome;
import com.adeptj.modules.jaxrs.api.JaxRSAuthenticator;
import com.adeptj.modules.jaxrs.api.JaxRSResource;
import com.adeptj.modules.jaxrs.api.UsernamePasswordCredential;
import com.adeptj.modules.jaxrs.core.jwt.JwtCookieService;
import com.adeptj.modules.security.jwt.JwtService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * JAX-RS resource for issuance of JWT basis the successful authentication by {@link JaxRSAuthenticator}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSResource(name = "jwt-authentication")
@Path("/token-auth/_j_security_check")
@Component(service = JwtAuthenticationResource.class)
public class JwtAuthenticationResource {

    private static final String J_USERNAME = "j_username";

    private static final String J_PASSWORD = "j_password";

    private final JwtService jwtService;

    private final JaxRSAuthenticator authenticator;

    private final JwtCookieService jwtCookieService;

    @Activate
    public JwtAuthenticationResource(@Reference JwtService jwtService,
                                     @Reference JaxRSAuthenticator authenticator,
                                     @Reference JwtCookieService jwtCookieService) {
        this.jwtService = jwtService;
        this.authenticator = authenticator;
        this.jwtCookieService = jwtCookieService;
    }

    /**
     * Create Jwt for the username with given credentials.
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     * @return JAX-RS Response either having a Jwt or Http error 401
     */
    @POST
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response createJwt(@NotEmpty @FormParam(J_USERNAME) String username,
                              @NotEmpty @FormParam(J_PASSWORD) String password) {
        Response response;
        UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
        try {
            JaxRSAuthenticationOutcome outcome = this.authenticator.authenticate(credential);
            if (outcome == null || outcome.isEmpty()) {
                response = Response.status(UNAUTHORIZED).build();
            } else {
                String jwt = this.jwtService.createJwt(username, outcome);
                if (this.jwtCookieService.isJwtCookieEnabled()) {
                    NewCookie cookie = this.jwtCookieService.createJwtCookie(jwt);
                    response = Response.ok().cookie(cookie).build();
                } else {
                    response = Response.ok().header(AUTHORIZATION, jwt).build();
                }
            }
        } finally {
            credential.clear();
        }
        return response;
    }
}
