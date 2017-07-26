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

import org.jboss.resteasy.spi.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Handles all the exceptions coming out of resource method calls.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Provider
public class DefaultExceptionHandler implements ExceptionMapper<ApplicationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public Response toResponse(ApplicationException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response.serverError()
                .type(APPLICATION_JSON)
                .entity(new ErrorResponse("ERROR", exception.getMessage()))
                .build();
    }
}
