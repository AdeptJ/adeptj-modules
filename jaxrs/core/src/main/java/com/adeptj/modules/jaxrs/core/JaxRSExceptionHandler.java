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

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.JSON_KEY_ERROR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * An {@link ExceptionMapper} for JaxRSException.
 * <p>
 * Sends the unhandled JaxRSException's message coming out of resource method calls
 * as JSON response if showException is set as true otherwise a generic error message is sent.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
public class JaxRSExceptionHandler implements ExceptionMapper<JaxRSException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSExceptionHandler.class);

    private boolean showException;

    public JaxRSExceptionHandler(boolean showException) {
        this.showException = showException;
    }

    @Override
    public Response toResponse(JaxRSException exception) {
        if (exception.isLogException()) {
            LOGGER.error(exception.getMessage(), exception);
        }
        String mediaType = exception.getMediaType();
        Object entity = exception.getEntity();
        if (entity == null) {
            entity = new ErrorResponse(JSON_KEY_ERROR, exception, this.showException);
            // if entity was not set then no point in considering the mediaType.
            mediaType = APPLICATION_JSON;
        }
        return Response.status(exception.getStatus())
                .type(mediaType)
                .entity(entity)
                .build();
    }
}
