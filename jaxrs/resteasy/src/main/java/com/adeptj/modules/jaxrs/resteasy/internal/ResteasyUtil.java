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

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.ServiceReference;

import javax.servlet.ServletContext;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.KEY_PROVIDER_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.KEY_RESOURCE_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.RESTEASY_DEPLOYMENT;

/**
 * Utilities for RESTEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ResteasyUtil {

    private ResteasyUtil() {
    }

    static void addResteasyDeployment(@NotNull ServletContext servletContext, ResteasyDeployment deployment) {
        servletContext.setAttribute(RESTEASY_DEPLOYMENT, deployment);
    }

    static void removeResteasyDeployment(@NotNull ServletContext servletContext) {
        servletContext.removeAttribute(RESTEASY_DEPLOYMENT);
    }

    static <T> String getResourceName(@NotNull ServiceReference<T> reference) {
        return (String) reference.getProperty(KEY_RESOURCE_NAME);
    }

    static <T> String getProviderName(@NotNull ServiceReference<T> reference) {
        return (String) reference.getProperty(KEY_PROVIDER_NAME);
    }

    static <T> boolean isProvider(ServiceReference<T> reference) {
        return StringUtils.isNotEmpty(getProviderName(reference));
    }

    static <T> boolean isResource(ServiceReference<T> reference) {
        return StringUtils.isNotEmpty(getResourceName(reference));
    }
}
