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

import org.apache.commons.lang3.StringUtils;

/**
 * ErrorResponse to return in case of exception arise out of resource methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ErrorResponse {

    private static final String DEFAULT_ERROR_MSG = "Unexpected error, we are looking into it. Please try again later!!";

    private String error;

    public ErrorResponse(Exception ex, boolean sendExceptionMsg) {
        if (sendExceptionMsg) {
            this.error = StringUtils.isEmpty(ex.getMessage()) ? DEFAULT_ERROR_MSG : ex.getMessage();
        } else {
            this.error = DEFAULT_ERROR_MSG;
        }
    }

    public String getError() {
        return error;
    }
}
