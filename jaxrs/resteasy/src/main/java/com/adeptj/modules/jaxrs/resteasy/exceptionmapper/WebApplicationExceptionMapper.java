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

import com.adeptj.modules.commons.utils.JakartaJsonUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Priority;
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

    private static final String JSON_KEY_CODE = "code";

    private static final String JSON_KEY_MESSAGE = "message";

    /**
     * Returns a {@link Response} object with status from the {@link Response} hold by the type of
     * {@link WebApplicationException} currently being passed.
     *
     * @param exception a subtype of {@link WebApplicationException} or {@link WebApplicationException} itself.
     * @return a new Json {@link Response} with status from the {@link Response} hold by the type of
     * {@link WebApplicationException} currently being passed..
     */
    @Override
    public Response toResponse(@NotNull WebApplicationException exception) {
        Response currentResponse = exception.getResponse();
        return Response.status(currentResponse.getStatus())
                .type(APPLICATION_JSON)
                .entity(JakartaJsonUtil.getJsonBuilderFactory().createObjectBuilder()
                        .add(JSON_KEY_CODE, currentResponse.getStatus())
                        .add(JSON_KEY_MESSAGE, currentResponse.getStatusInfo().getReasonPhrase())
                        .build())
                .build();
    }
}
