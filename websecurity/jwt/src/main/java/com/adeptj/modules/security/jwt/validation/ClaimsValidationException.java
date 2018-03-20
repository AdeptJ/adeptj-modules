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

package com.adeptj.modules.security.jwt.validation;

/**
 * Original Exception must be wrapped and rethrown in case of exceptional scenarios while validating claims.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ClaimsValidationException extends RuntimeException {

    private static final long serialVersionUID = 1591499996984025127L;

    public ClaimsValidationException(String message) {
        super(message);
    }

    public ClaimsValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClaimsValidationException(Throwable cause) {
        super(cause);
    }
}
