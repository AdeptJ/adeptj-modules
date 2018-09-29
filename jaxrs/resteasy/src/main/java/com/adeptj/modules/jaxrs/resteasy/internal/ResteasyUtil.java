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
import java.util.Map;
import java.util.Set;

/**
 * Utilities for RESTEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ResteasyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ResteasyUtil() {
    }

    static void registerProviders(ResteasyProviderFactory rpf, ResteasyConfig config, ValidatorFactory vf) {
        rpf.register(new ValidatorContextResolver(vf))
                .register(new DefaultExceptionHandler(config.showException()))
                .register(new JaxRSExceptionHandler(config.showException()))
                .register(new WebApplicationExceptionHandler(config.showException()))
                .register(CorsFilterBuilder.newBuilder()
                        .allowCredentials(config.allowCredentials())
                        .corsMaxAge(config.corsMaxAge())
                        .exposedHeaders(config.exposedHeaders())
                        .allowedMethods(config.allowedMethods())
                        .allowedHeaders(config.allowedHeaders())
                        .allowedOrigins(config.allowedOrigins())
                        .build());
    }

    static void removeDefaultValidators(ResteasyProviderFactoryWrapper rpf) {
        Map<Class<?>, ?> contextResolvers = rpf.getContextResolvers();
        LOGGER.info("ContextResolver(s) prior to removal: [{}]", contextResolvers.size());
        contextResolvers.remove(GeneralValidator.class);
        contextResolvers.remove(GeneralValidatorCDI.class);
        LOGGER.info("ContextResolver(s) after removal: [{}]", contextResolvers.size());
    }

    static void removeProviderClasses(ResteasyProviderFactoryWrapper rpf) {
        Set<Class<?>> providerClasses = rpf.getProviderClasses();
        LOGGER.info("ProviderClasses prior to removal: [{}]", providerClasses.size());
        providerClasses.remove(ValidatorContextResolver.class);
        providerClasses.remove(ValidatorContextResolverCDI.class);
        LOGGER.info("ProviderClasses after removal: [{}]", providerClasses.size());
    }

    static void removeJaxRSProvider(ResteasyProviderFactoryWrapper rpfWrapper, Object provider) {
        if (rpfWrapper.getProviderInstances().remove(provider)) {
            LOGGER.info("Removed JAX-RS Provider: [{}]", provider);
        } else {
            LOGGER.warn("Could not remove JAX-RS Provider: [{}]", provider);
        }
    }

    static boolean isNotAnnotatedWithPath(Object resource) {
        return !resource.getClass().isAnnotationPresent(Path.class);
    }
}
