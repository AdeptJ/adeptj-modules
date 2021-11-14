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
package com.adeptj.modules.restclient.plugin;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;

/**
 * Plugin for injecting the Authorization header in matching request paths.
 * <p>
 * RestClient consumers should implemented this interface if they need seamless injection of Authorization
 * header in matching request paths.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface AuthorizationHeaderPlugin {

    /**
     * Should return the type of authentication scheme such as Bearer or Basic
     *
     * @return authentication scheme such as Bearer or Basic
     */
    @NotNull
    String getType();

    /**
     * Should return the Authorization header value.
     *
     * @return Authorization header value
     */
    @NotNull
    String getValue();

    /**
     * Should return the request path patterns on which the Authorization header to be injected.
     * <p>
     * Example: /api/** or /api/users/* etc.
     * <p>
     * '?' - matches one character
     * '*' - matches zero or more characters
     * '**' - matches zero or more directories in a path
     *
     * @return request path patterns
     */
    @NotNull
    List<String> getPathPatterns();
}
