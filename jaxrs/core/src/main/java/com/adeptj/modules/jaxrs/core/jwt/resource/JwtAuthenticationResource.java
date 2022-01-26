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
import com.adeptj.modules.security.jwt.JwtService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * JAX-RS resource for issuance of JWT basis the successful authentication by {@link JaxRSAuthenticator}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSResource(name = "jwt")
@Path("/token-auth/_j_security_check")
@Designate(ocd = JwtCookieConfig.class)
@Component(service = JwtAuthenticationResource.class)
public class JwtAuthenticationResource {

    private static final String J_USERNAME = "j_username";

    private static final String J_PASSWORD = "j_password";

    private final JwtService jwtService;

    private final JaxRSAuthenticator authenticator;

    @Activate
    public JwtAuthenticationResource(@Reference JwtService jwtService, @Reference JaxRSAuthenticator authenticator) {
        this.jwtService = jwtService;
        this.authenticator = authenticator;
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
        UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
        try {
            JaxRSAuthenticationOutcome outcome = this.authenticator.authenticate(credential);
            return outcome == null || outcome.isEmpty()
                    ? Response.status(UNAUTHORIZED).build()
                    : JaxRSUtil.createResponseWithJwt(this.jwtService.createJwt(username, outcome));
        } finally {
            credential.clear();
        }
    }

    // <<---------------------------------------- OSGi INTERNAL ------------------------------------------>>

    @Modified
    protected void update(@NotNull JwtCookieConfig cookieConfig) {
        JwtCookieConfigHolder.getInstance().setJwtCookieConfig(cookieConfig);
    }
}
