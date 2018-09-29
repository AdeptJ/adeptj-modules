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

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ext.ContextResolver;
import java.util.Map;
import java.util.Set;

/**
 * ResteasyProviderFactoryWrapper.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyProviderFactoryWrapper extends ResteasyProviderFactory {

    @Override
    public Map<Class<?>, MediaTypeMap<SortedKey<ContextResolver>>> getContextResolvers() {
        return super.getContextResolvers();
    }

    @Override
    public Set<Class<?>> getProviderClasses() {
        return this.providerClasses;
    }

    @Override
    public Set<Object> getProviderInstances() {
        return this.providerInstances;
    }
}
