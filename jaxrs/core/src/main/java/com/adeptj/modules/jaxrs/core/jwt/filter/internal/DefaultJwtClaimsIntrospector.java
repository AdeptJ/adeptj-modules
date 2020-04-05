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

package com.adeptj.modules.jaxrs.core.jwt.filter.internal;

import com.adeptj.modules.jaxrs.core.jwt.JwtClaimsIntrospector;
import com.adeptj.modules.security.jwt.JwtUtil;

import java.util.Map;

/**
 * Default implementation of {@link JwtClaimsIntrospector}, which just checks whether the Jwt is expired or not.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class DefaultJwtClaimsIntrospector implements JwtClaimsIntrospector {

    static final JwtClaimsIntrospector INSTANCE = new DefaultJwtClaimsIntrospector();

    @Override
    public boolean introspect(Map<String, Object> claims) {
        return !JwtUtil.isExpired(claims);
    }
}
