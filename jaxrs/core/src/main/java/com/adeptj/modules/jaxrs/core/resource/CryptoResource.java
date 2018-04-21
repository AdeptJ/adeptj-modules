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

import com.adeptj.modules.commons.utils.service.CryptoService;
import com.adeptj.modules.jaxrs.core.jwt.RequiresJwt;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.jaxrs.core.resource.CryptoResource.RESOURCE_BASE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


/**
 * JAX-RS resource for creating the hash of the plain text string passed.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path("/utils/crypto")
@Component(immediate = true, service = CryptoResource.class, property = RESOURCE_BASE)
public class CryptoResource {

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=crypto";

    @Reference
    private CryptoService cryptoService;

    /**
     * Creates the hash of the plain text string passed.
     *
     * @param plainText the text to be hashed.
     * @return JAX-RS {@link Response} having the generated hash and the salt in JSON format.
     */
    @POST
    @Path("/create-salt-hash-pair")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @Produces(APPLICATION_JSON)
    @RequiresJwt
    public Response createSaltHashPair(@NotEmpty @FormParam("plainText") String plainText) {
        return Response.ok((this.cryptoService.getSaltHashPair(plainText))).build();
    }
}
