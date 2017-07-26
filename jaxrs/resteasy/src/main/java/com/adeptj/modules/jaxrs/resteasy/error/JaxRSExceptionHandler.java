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

import com.adeptj.modules.jaxrs.core.JaxRSException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * {@link ExceptionMapper} for JaxRSException.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
public class JaxRSExceptionHandler implements ExceptionMapper<JaxRSException> {

    private boolean showException;

    public JaxRSExceptionHandler(boolean showException) {
        this.showException = showException;
    }

    @Override
    public Response toResponse(JaxRSException exception) {
        String mediaType = exception.getMediaType();
        Object entity = exception.getEntity();
        if (entity == null) {
            entity = new ErrorResponse("ERROR", exception.getMessage(), this.showException);
            mediaType = APPLICATION_JSON;
        }
        return Response.status(exception.getStatus())
                .type(mediaType)
                .entity(entity)
                .build();
    }
}
