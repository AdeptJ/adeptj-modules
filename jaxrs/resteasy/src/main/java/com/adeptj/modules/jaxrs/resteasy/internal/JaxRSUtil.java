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

import com.adeptj.modules.jaxrs.core.JaxRSExceptionHandler;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

/**
 * Utilities for RestEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JaxRSUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSUtil.class);

    private static final String METHOD_GET_CTX_RESOLVERS = "getContextResolvers";

    private static final String FIELD_PROVIDER_INSTANCES = "providerInstances";

    private static final String DELIMITER_COMMA = ",";

    private static final boolean FORCE_ACCESS = true;

    private JaxRSUtil() {
    }

    static void registerDefaultJaxRSProviders(ResteasyProviderFactory providerFactory, JaxRSCoreConfig config) {
        providerFactory
                .register(new ValidatorContextResolver())
                .register(createCorsFilter(config))
                .register(new DefaultExceptionHandler(config.showException()))
                .register(new JaxRSExceptionHandler(config.showException()));
    }

    static void removeJaxRSProvider(ResteasyProviderFactory providerFactory, Object provider) {
        if (provider == null) {
            return;
        }
        try {
            Field providers = getDeclaredField(ResteasyProviderFactory.class, FIELD_PROVIDER_INSTANCES, FORCE_ACCESS);
            if (Set.class.cast(providers.get(providerFactory)).remove(provider)) {
                LOGGER.info("Removed JAX-RS Provider: [{}]", provider);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while removing JAX-RS Provider!!", ex);
        }
    }

    static void removeDefaultValidators(ResteasyProviderFactory providerFactory) {
        try {
            // First remove the default RESTEasy GeneralValidator and GeneralValidatorCDI.
            // After that we will register our ValidatorContextResolver.
            Map<?, ?> contextResolvers = Map.class.cast(invokeMethod(providerFactory, FORCE_ACCESS, METHOD_GET_CTX_RESOLVERS,
                    null, null));
            LOGGER.info("ContextResolver(s) prior to removal: [{}]", contextResolvers.size());
            contextResolvers.remove(GeneralValidator.class);
            contextResolvers.remove(GeneralValidatorCDI.class);
            LOGGER.info("ContextResolver(s) after removal: [{}]", contextResolvers.size());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            LOGGER.error("Exception while removing RESTEasy Validators", ex);
        }
    }

    static ServiceTracker<Object, Object> openProviderServiceTracker(BundleContext context, ResteasyProviderFactory factory) {
        ServiceTracker<Object, Object> providerTracker = new JaxRSProviderTracker(context, factory);
        providerTracker.open();
        return providerTracker;
    }

    static ServiceTracker<Object, Object> openResourceServiceTracker(BundleContext context, Registry registry) {
        ServiceTracker<Object, Object> resourceTracker = new JaxRSResourceTracker(context, registry);
        resourceTracker.open();
        return resourceTracker;
    }

    private static CorsFilter createCorsFilter(JaxRSCoreConfig config) {
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setAllowCredentials(config.allowCredentials());
        corsFilter.setAllowedMethods(config.allowedMethods());
        corsFilter.setCorsMaxAge(config.corsMaxAge());
        corsFilter.setAllowedHeaders(Arrays.stream(config.allowedHeaders()).collect(joining(DELIMITER_COMMA)));
        corsFilter.setExposedHeaders(Arrays.stream(config.exposedHeaders()).collect(joining(DELIMITER_COMMA)));
        corsFilter.getAllowedOrigins().addAll(Arrays.stream(config.allowedOrigins()).collect(toSet()));
        return corsFilter;
    }
}
