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
import com.adeptj.modules.jaxrs.core.WebApplicationExceptionHandler;
import com.adeptj.modules.jaxrs.resteasy.ResteasyConfig;
import org.jboss.resteasy.plugins.validation.ValidatorContextResolverCDI;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidatorFactory;
import javax.ws.rs.Path;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.FIELD_PROVIDER_CLASSES;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.FIELD_PROVIDER_INSTANCES;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.FORCE_ACCESS;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.METHOD_GET_CTX_RESOLVERS;
import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

/**
 * Utilities for RESTEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ResteasyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ResteasyUtil() {
    }

    static void registerInternalProviders(ResteasyProviderFactory rpf, ResteasyConfig config, ValidatorFactory vf) {
        rpf.register(new ValidatorContextResolver(vf))
                .register(new DefaultExceptionHandler(config.showException()))
                .register(new JaxRSExceptionHandler(config.showException()))
                .register(new WebApplicationExceptionHandler(config.showException()))
                .register(new FluentCorsFilter()
                        .allowCredentials(config.allowCredentials())
                        .corsMaxAge(config.corsMaxAge())
                        .exposedHeaders(config.exposedHeaders())
                        .allowedMethods(config.allowedMethods())
                        .allowedHeaders(config.allowedHeaders())
                        .allowedOrigins(config.allowedOrigins())
                        .corsFilter());
    }

    static void removeInternalValidators(ResteasyProviderFactory rpf) {
        try {
            Map<?, ?> contextResolvers = (Map) invokeMethod(rpf, FORCE_ACCESS, METHOD_GET_CTX_RESOLVERS, null, null);
            LOGGER.info("ContextResolver(s) prior to removal: [{}]", contextResolvers.size());
            contextResolvers.remove(GeneralValidator.class);
            contextResolvers.remove(GeneralValidatorCDI.class);
            LOGGER.info("ContextResolver(s) after removal: [{}]", contextResolvers.size());
            Field field = getDeclaredField(rpf.getClass(), FIELD_PROVIDER_CLASSES, FORCE_ACCESS);
            Set<?> providerClasses = (Set) readField(field, rpf, FORCE_ACCESS);
            providerClasses.remove(org.jboss.resteasy.plugins.validation.ValidatorContextResolver.class);
            providerClasses.remove(ValidatorContextResolverCDI.class);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            LOGGER.error("Exception while removing RESTEasy Validators", ex);
        }
    }

    static void removeJaxRSProvider(ResteasyProviderFactory providerFactory, Object provider) {
        if (provider == null) {
            LOGGER.warn("JAX-RS Provider is null!!");
            return;
        }
        try {
            Field providers = getDeclaredField(providerFactory.getClass(), FIELD_PROVIDER_INSTANCES, FORCE_ACCESS);
            if (((Set) readField(providers, providerFactory, FORCE_ACCESS)).remove(provider)) {
                LOGGER.info("Removed JAX-RS Provider: [{}]", provider);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while removing JAX-RS Provider!!", ex);
        }
    }

    static boolean isNotAnnotatedWithPath(Object resource) {
        return resource == null || !resource.getClass().isAnnotationPresent(Path.class);
    }
}
