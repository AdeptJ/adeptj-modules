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

package com.adeptj.modules.data.jpa.eclipselink.internal;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DEPLOY_ON_STARTUP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.config.PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LOGGER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_PARAMETERS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SESSION_CUSTOMIZER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SHARED_CACHE_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATION_MODE;

/**
 * Utility methods for {@link jakarta.persistence.EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JpaProperties {

    static @NotNull Map<String, Object> create(@NotNull EntityManagerFactoryConfig config) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(DDL_GENERATION, config.ddl_generation());
        properties.put(DDL_GENERATION_MODE, config.ddl_generation_output_mode());
        properties.put(SESSION_CUSTOMIZER, new QueryRetryCustomizer(config.query_retry_attempt_count()));
        properties.put(DEPLOY_ON_STARTUP, config.deploy_on_startup());
        properties.put(LOGGING_LEVEL, config.logging_level());
        properties.put(LOGGING_LOGGER, config.eclipselink_slf4j_logger_fqcn());
        properties.put(LOGGING_PARAMETERS, Boolean.toString(config.log_query_parameters()));
        // Add all loggers
        Stream.of(config.eclipselink_loggers())
                .filter(StringUtils::isNotEmpty)
                .forEach(logger -> properties.put(logger, config.logging_level()));
        properties.put(TRANSACTION_TYPE, config.persistence_unit_transaction_type());
        properties.put(ECLIPSELINK_PERSISTENCE_XML, config.persistence_xml_location());
        properties.put(SHARED_CACHE_MODE, config.shared_cache_mode());
        properties.put(VALIDATION_MODE, config.validation_mode());
        if (config.use_exception_handler()) {
            properties.put(EXCEPTION_HANDLER_CLASS, config.exception_handler_fqcn());
        }
        // Extra properties are in [key=value] format.
        Stream.of(config.jpa_properties())
                .filter(StringUtils::isNotEmpty)
                .map(row -> row.split(EQ))
                .filter(parts -> ArrayUtils.getLength(parts) == 2)
                .forEach(parts -> properties.put(parts[0].trim(), parts[1].trim()));
        return properties;
    }
}
