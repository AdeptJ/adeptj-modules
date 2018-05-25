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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * An {@link ExceptionMapper} for {@link WebApplicationException}.
 * <p>
 * Logs if {@link WebApplicationExceptionHandler#logException} set as true and then returns the {@link Response}
 * contained in {@link WebApplicationException}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
public class WebApplicationExceptionHandler implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionHandler.class);

    private boolean logException;

    public WebApplicationExceptionHandler(boolean logException) {
        this.logException = logException;
    }

    @Override
    public Response toResponse(WebApplicationException exception) {
        if (this.logException) {
            LOGGER.error(exception.getMessage(), exception);
        }
        Response response = exception.getResponse();
        if (response == null) {
            response = Response.serverError()
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(ErrorResponse.DEFAULT_ERROR_MSG)
                    .build();
        }
        return response;
    }
}
