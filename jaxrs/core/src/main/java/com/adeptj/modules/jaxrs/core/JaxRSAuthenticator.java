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
package com.adeptj.modules.jaxrs.core;

import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import java.util.Arrays;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * JaxRSAuthenticator.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Path("/auth")
@Component(immediate = true, service = JaxRSAuthenticator.class, property = "osgi.jaxrs.resource.base=auth")
public class JaxRSAuthenticator {

    @Reference
    private JaxRSAuthenticationRepository authenticationRepository;

    @Reference
    private JwtService jwtService;

    @POST
    @Path("jwt/issue")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response issueToken(@NotNull @FormParam("subject") String subject, @NotNull @FormParam("password") String password) {
        Response response;
        try {
            // First authenticate the user using the credentials provided.
            JaxRSAuthenticationInfo authenticationInfo = this.authenticationRepository.getAuthenticationInfo(subject);
            if (authenticationInfo == null) {
                response = Response.status(UNAUTHORIZED).build();
            } else {
                if (authenticationInfo.getSubject().equals(subject)
                        && Arrays.equals(authenticationInfo.getPassword(), password.toCharArray())) {
                    // All well here, now issue a token for the Subject
                    response = Response.ok().header(AUTHORIZATION, this.jwtService.issueToken(subject)).build();
                } else {
                    response = Response.status(UNAUTHORIZED).build();
                }
            }
        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
        return response;
    }

    @GET
    @Path("jwt/check")
    @RequiresJwtCheck
    public Response checkJwt() {
        return Response.ok("JWT is valid!!").build();
    }
}
