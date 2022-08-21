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

package com.adeptj.modules.jaxrs.resteasy.contextresolver;

import com.adeptj.modules.commons.utils.JavaxJsonUtil;

import javax.annotation.Priority;
import javax.json.bind.Jsonb;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonbContextResolver.PRIORITY;

/**
 * ContextResolver for Jakarta's {@link Jsonb}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Produces({"application/json", "application/*+json", "text/json"})
@Priority(PRIORITY)
@Provider
public class JsonbContextResolver implements ContextResolver<Jsonb> {

    static final int PRIORITY = 5000;

    @Override
    public Jsonb getContext(Class<?> type) {
        // type check not needed because Resteasy will call this method for serializing arbitrary objects.
        return JavaxJsonUtil.getJsonb();
    }
}
