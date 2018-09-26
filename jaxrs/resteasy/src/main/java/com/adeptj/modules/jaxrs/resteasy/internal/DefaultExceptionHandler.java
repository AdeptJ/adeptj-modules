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

package com.adeptj.modules.jaxrs.resteasy.internal;

import com.adeptj.modules.jaxrs.core.ErrorResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.JSON_KEY_ERROR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * An {@link ExceptionMapper} for ApplicationException.
 * <p>
 * Sends the unhandled exception's message coming out of resource method calls as JSON response if showException
 * is set as true otherwise a generic error message is sent.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
public class DefaultExceptionHandler implements ExceptionMapper<ApplicationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private boolean showException;

    DefaultExceptionHandler(boolean showException) {
        this.showException = showException;
    }

    @Override
    public Response toResponse(ApplicationException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response.serverError()
                .type(APPLICATION_JSON)
                .entity(new ErrorResponse(JSON_KEY_ERROR, exception, this.showException))
                .build();
    }
}
