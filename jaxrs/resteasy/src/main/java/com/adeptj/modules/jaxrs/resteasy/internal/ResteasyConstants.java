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

import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * Constants for RESTEasy modules.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ResteasyConstants {

    static final String RESTEASY_PROXY_SERVLET_NAME = "AdeptJ RESTEasy ProxyServlet";

    static final String RESTEASY_DISPATCHER_SERVLET_PATH = "/";

    static final String KEY_PROVIDER_NAME = "osgi.jaxrs.provider.name";

    static final String KEY_RESOURCE_NAME = "osgi.jaxrs.resource.name";

    static final String COMPOSITE_TRACKER_FILTER = "(|(osgi.jaxrs.provider.name=*)(osgi.jaxrs.resource.name=*))";

    static final String VALUE_TRUE = "true";

    static final String RESTEASY_DEPLOYMENT = ResteasyDeployment.class.getName();

    private ResteasyConstants() {
    }
}
