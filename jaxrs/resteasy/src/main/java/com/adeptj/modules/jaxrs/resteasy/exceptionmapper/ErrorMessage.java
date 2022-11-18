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

import org.apache.commons.lang3.StringUtils;

import jakarta.ws.rs.core.Response;

/**
 * A pojo for 500 error info serialization.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ErrorMessage {

    private final int status;

    private final String message;

    private StackTraceElement[] stackTrace;

    public ErrorMessage(Exception ex, String defaultErrorMessage, boolean sendExceptionTrace) {
        this.status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        if (sendExceptionTrace) {
            this.message = StringUtils.isEmpty(ex.getMessage()) ? ex.getClass().getName() : ex.getMessage();
            this.stackTrace = ex.getStackTrace();
        } else {
            this.message = defaultErrorMessage;
        }
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }
}
