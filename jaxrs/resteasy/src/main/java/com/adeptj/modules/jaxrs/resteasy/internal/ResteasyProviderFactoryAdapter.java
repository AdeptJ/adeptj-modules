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

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.util.Set;

/**
 * The {@link ResteasyProviderFactory} adapter provides the access to {@link #providerInstances} which is used in adding
 * and removing the provider instances through OSGi {@link org.osgi.util.tracker.ServiceTracker} mechanism.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyProviderFactoryAdapter extends ResteasyProviderFactory {

    /**
     * See class header for description.
     *
     * @return the provider instances.
     */
    @Override
    public Set<Object> getProviderInstances() {
        return this.providerInstances;
    }
}
