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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.resteasy.exceptionmapper.GenericExceptionMapper.PRIORITY;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * A global {@link ExceptionMapper} for handling all uncaught exception types.
 * <p>
 * Sends the uncaught exception's trace in response if sendExceptionTrace is set as true otherwise a generic error message is sent.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Priority(PRIORITY)
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    static final int PRIORITY = 6500;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String DEFAULT_ERROR_MSG = "Unexpected error, we are looking into it. Please try again later!!";

    private final boolean sendExceptionTrace;

    public GenericExceptionMapper(boolean sendExceptionTrace) {
        this.sendExceptionTrace = sendExceptionTrace;
    }

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response.serverError()
                .type(TEXT_PLAIN)
                .entity(this.sendExceptionTrace ? ExceptionUtils.getStackTrace(exception) : DEFAULT_ERROR_MSG)
                .build();
    }
}
