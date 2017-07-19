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

import com.adeptj.modules.jaxrs.core.api.JaxRSAuthenticationRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthenticator.class);

    private static final String BIND_JWT_SERVICE = "bindJwtService";

    private static final String UNBIND_JWT_SERVICE = "unbindJwtService";

    private static final String BIND_AUTH_SERVICE = "bindJaxRSAuthenticationRepository";

    private static final String UNBIND_AUTH_SERVICE = "unbindJaxRSAuthenticationRepository";

    @Reference(
            service = JaxRSAuthenticationRepository.class,
            bind = BIND_AUTH_SERVICE,
            unbind = UNBIND_AUTH_SERVICE,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC
    )
    private volatile List<JaxRSAuthenticationRepository> authRepositories = new ArrayList<>();

    private volatile Map<String, JaxRSAuthenticationRepository> authRepositoryMap = new HashMap<>();

    @Reference(
            bind = BIND_JWT_SERVICE,
            unbind = UNBIND_JWT_SERVICE,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC
    )
    private JwtService jwtService;

    @POST
    @Path("jwt/issue")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response issueToken(@NotNull @FormParam("subject") String subject, @NotNull @FormParam("password") String password) {
        if (this.jwtService == null) {
            LOGGER.warn("Can't issue token as JwtService unavailable!");
            return Response.status(UNAUTHORIZED).entity("JwtService unavailable!!").build();
        }
        Response response;
        try {
            // First authenticate the user using the credentials provided.
            JaxRSAuthenticationInfo authenticationInfo = this.handleSecurity(subject, password);
            if (authenticationInfo == null) {
                response = Response.status(UNAUTHORIZED).entity("Invalid credentials!!").build();
            } else {
                if (authenticationInfo.getSubject().equals(subject)
                        && Arrays.equals(authenticationInfo.getPassword(), password.toCharArray())) {
                    // All well here, now issue a token for the Subject
                    Map<String, String> payload = new HashMap<>();
                    payload.put("subject", subject);
                    response = Response.ok().header(AUTHORIZATION, this.jwtService.issueToken(payload)).build();
                } else {
                    response = Response.status(UNAUTHORIZED).entity("Invalid credentials!!").build();
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
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

    private JaxRSAuthenticationInfo handleSecurity(String subject, String password) {
        for (Map.Entry<String, JaxRSAuthenticationRepository> entry : this.authRepositoryMap.entrySet()) {
            JaxRSAuthenticationInfo authenticationInfo = entry.getValue().getAuthenticationInfo(subject, password);
            if (authenticationInfo != null) {
                return authenticationInfo;
            }
        }
        return null;
    }

    // LifeCycle Methods

    protected void bindJaxRSAuthenticationRepository(JaxRSAuthenticationRepository authRepository) {
        LOGGER.info("Binding JaxRSAuthenticationRepository: [{}]", authRepository);
        this.authRepositoryMap.put(authRepository.getName(), authRepository);
        this.authRepositories.add(authRepository);
    }

    protected void unbindJaxRSAuthenticationRepository(JaxRSAuthenticationRepository authRepository) {
        LOGGER.info("Unbinding JaxRSAuthenticationRepository: [{}]", authRepository);
        this.authRepositoryMap.remove(authRepository.getName());
        this.authRepositories.remove(authRepository);
    }

    protected void bindJwtService(JwtService jwtService) {
        LOGGER.info("Binding JwtService: [{}]", jwtService);
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) {
        LOGGER.info("Unbinding JwtService: [{}]", jwtService);
        this.jwtService = null;
    }

}
