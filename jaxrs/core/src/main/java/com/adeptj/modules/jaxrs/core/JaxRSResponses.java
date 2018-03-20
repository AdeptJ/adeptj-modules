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

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Utilities for JAX-RS {@link Response}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JaxRSResponses {

    private JaxRSResponses() {
    }

    public static Response okWithCookie(NewCookie cookie) {
        return Response.ok().cookie(cookie).build();
    }

    public static Response okWithHeader(String name, String value) {
        return Response.ok().header(name, value).build();
    }

    public static Response ok(Object entity) {
        return Response.ok(entity).build();
    }

    public static Response unavailable() {
        return Response.status(SERVICE_UNAVAILABLE).build();
    }

    public static Response unauthorized() {
        return Response.status(UNAUTHORIZED).build();
    }

    public static Response forbidden() {
        return Response.status(FORBIDDEN).build();
    }
}
