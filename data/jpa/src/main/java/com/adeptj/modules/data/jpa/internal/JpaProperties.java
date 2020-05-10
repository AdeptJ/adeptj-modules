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

package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.JpaExceptionHandler;
import com.adeptj.modules.data.jpa.SLF4JLogger;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static com.adeptj.modules.data.jpa.JpaConstants.SHARED_CACHE_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DEPLOY_ON_STARTUP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.config.PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LOGGER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_PARAMETERS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SESSION_CUSTOMIZER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATION_MODE;

/**
 * Utility methods for {@link javax.persistence.EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JpaProperties {

    static @NotNull Map<String, Object> from(@NotNull EntityManagerFactoryConfig config) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(DDL_GENERATION, config.ddlGeneration());
        properties.put(DDL_GENERATION_MODE, config.ddlGenerationOutputMode());
        properties.put(SESSION_CUSTOMIZER, new QueryRetryCustomizer(config.queryRetryAttemptCount()));
        // DEPLOY_ON_STARTUP must be a string value
        properties.put(DEPLOY_ON_STARTUP, Boolean.toString(config.deployOnStartup()));
        properties.put(LOGGING_LEVEL, config.loggingLevel());
        properties.put(LOGGING_LOGGER, SLF4JLogger.class.getName());
        properties.put(LOGGING_PARAMETERS, Boolean.toString(config.logQueryParameters()));
        // Add all loggers
        Stream.of(config.eclipselinkLoggers()).forEach(logger -> properties.put(logger, config.loggingLevel()));
        properties.put(TRANSACTION_TYPE, config.persistenceUnitTransactionType());
        properties.put(ECLIPSELINK_PERSISTENCE_XML, config.persistenceXmlLocation());
        properties.put(SHARED_CACHE_MODE, config.sharedCacheMode());
        properties.put(VALIDATION_MODE, config.validationMode());
        if (config.useExceptionHandler()) {
            properties.put(EXCEPTION_HANDLER_CLASS, JpaExceptionHandler.class.getName());
        }
        // Extra properties are in [key=value] format.
        Stream.of(config.jpaProperties())
                .filter(row -> ArrayUtils.getLength(row.split(EQ)) == 2)
                .map(row -> row.split(EQ))
                .forEach(mapping -> properties.put(mapping[0], mapping[1]));
        return properties;
    }
}
