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

package com.adeptj.modules.jaxrs.resteasy.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;

/**
 * ExceptionMapper for Http 405
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Provider
public class NotAllowedExceptionHandler implements ExceptionMapper<NotAllowedException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotAllowedExceptionHandler.class);

    @Override
    public Response toResponse(NotAllowedException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response.status(METHOD_NOT_ALLOWED)
                .type(APPLICATION_JSON)
                .entity(new ErrorResponse("ERROR", exception.getMessage()))
                .build();
    }
}
