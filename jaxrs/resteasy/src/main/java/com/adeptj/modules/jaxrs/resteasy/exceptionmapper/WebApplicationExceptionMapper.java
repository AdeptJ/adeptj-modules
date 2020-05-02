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

package com.adeptj.modules.jaxrs.resteasy.exceptionmapper;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Priority;
import javax.json.Json;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.resteasy.exceptionmapper.WebApplicationExceptionMapper.PRIORITY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * An {@link ExceptionMapper} for handling exceptions of type {@link WebApplicationException}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Priority(PRIORITY)
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    static final int PRIORITY = 7000;

    @Override
    public Response toResponse(@NotNull WebApplicationException exception) {
        Response response = exception.getResponse();
        return Response.status(response.getStatus())
                .type(APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("code", response.getStatus())
                        .add("message", exception.getMessage()).build())
                .build();
    }
}
