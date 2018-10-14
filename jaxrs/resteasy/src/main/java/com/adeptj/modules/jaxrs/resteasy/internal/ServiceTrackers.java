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

import com.adeptj.modules.commons.utils.OSGiUtil;
import org.jboss.resteasy.core.Dispatcher;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Helper that manages the lifecycle of {@link ServiceTracker} objects for JAX-RS resources and providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class ServiceTrackers {

    private final ServiceTracker<Object, Object> providerTracker;

    private final ServiceTracker<Object, Object> resourceTracker;

    ServiceTrackers(BundleContext context, Dispatcher dispatcher) {
        this.providerTracker = new ProviderTracker(context, dispatcher.getProviderFactory());
        this.resourceTracker = new ResourceTracker(context, dispatcher.getRegistry());
    }

    void openAll() {
        this.providerTracker.open();
        this.resourceTracker.open();
    }

    void closeAll() {
        OSGiUtil.closeQuietly(this.providerTracker);
        OSGiUtil.closeQuietly(this.resourceTracker);
    }
}
