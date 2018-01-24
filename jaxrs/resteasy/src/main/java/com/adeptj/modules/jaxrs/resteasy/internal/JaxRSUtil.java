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

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.commons.utils.OSGiUtils;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;

/**
 * Utilities for RestEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JaxRSUtil {

    private static final String FIELD_CTX_RESOLVERS = "contextResolvers";

    private static final String FIELD_PROVIDER_INSTANCES = "providerInstances";

    private static final String PROVIDER_FILTER_EXPR = "(&(objectClass=*)(osgi.jaxrs.provider=*))";

    private static final String RES_FILTER_EXPR = "(&(objectClass=*)(osgi.jaxrs.resource.base=*))";

    private static final String DELIMITER_COMMA = ",";

    private JaxRSUtil() {
    }

    static void removeJaxRSProvider(ResteasyProviderFactory providerFactory, Object provider) {
        if (provider == null) {
            return;
        }
        try {
            if (Set.class.cast(getDeclaredField(ResteasyProviderFactory.class, FIELD_PROVIDER_INSTANCES, true)
                    .get(providerFactory))
                    .remove(provider)) {
                Loggers.get(JaxRSUtil.class).info("Removed JAX-RS Provider: [{}]", provider);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Loggers.get(JaxRSUtil.class).error("Exception while removing JAX-RS Provider!!", ex);
        }
    }

    static void removeDefaultValidators(ResteasyProviderFactory providerFactory) {
        try {
            // First remove the default RESTEasy GeneralValidator and GeneralValidatorCDI.
            // After that we will register our ValidatorContextResolver.
            Map<?, ?> contextResolvers = Map.class.cast(getDeclaredField(ResteasyProviderFactory.class,
                    FIELD_CTX_RESOLVERS, true).get(providerFactory));
            contextResolvers.remove(GeneralValidator.class);
            contextResolvers.remove(GeneralValidatorCDI.class);
            Loggers.get(JaxRSUtil.class).info("Removed RESTEasy Default Validators!!");
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Loggers.get(JaxRSUtil.class).error("Exception while removing RESTEasy Validators", ex);
        }
    }

    static CorsFilter createCorsFilter(JaxRSCoreConfig config) {
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setAllowCredentials(config.allowCredentials());
        corsFilter.setAllowedMethods(config.allowedMethods());
        corsFilter.setCorsMaxAge(config.corsMaxAge());
        corsFilter.setAllowedHeaders(Arrays.stream(config.allowedHeaders()).collect(joining(DELIMITER_COMMA)));
        corsFilter.setExposedHeaders(Arrays.stream(config.exposedHeaders()).collect(joining(DELIMITER_COMMA)));
        corsFilter.getAllowedOrigins().addAll(Arrays.stream(config.allowedOrigins()).collect(toSet()));
        return corsFilter;
    }

    static ServiceTracker<Object, Object> getProviderServiceTracker(BundleContext context, ResteasyProviderFactory factory) {
        ServiceTracker<Object, Object> providerTracker = new ServiceTracker<>(context,
                OSGiUtils.anyServiceFilter(context, PROVIDER_FILTER_EXPR),
                new JaxRSProviders(context, factory));
        providerTracker.open();
        return providerTracker;
    }


    static ServiceTracker<Object, Object> getResourceServiceTracker(BundleContext context, Registry registry) {
        ServiceTracker<Object, Object> resourceTracker = new ServiceTracker<>(context,
                OSGiUtils.anyServiceFilter(context, RES_FILTER_EXPR),
                new JaxRSResources(context, registry));
        resourceTracker.open();
        return resourceTracker;
    }

    static void closeServiceTracker(ServiceTracker<Object, Object> serviceTracker) {
        try {
            serviceTracker.close();
        } catch (Exception ex) {
            Loggers.get(JaxRSUtil.class).error(ex.getMessage(), ex);
        }
    }
}
