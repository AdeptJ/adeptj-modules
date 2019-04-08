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

package com.adeptj.modules.data.jpa.core;

import com.adeptj.modules.data.jpa.JpaExceptionHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static com.adeptj.modules.data.jpa.JpaConstants.PERSISTENCE_PROVIDER;
import static com.adeptj.modules.data.jpa.JpaConstants.SHARED_CACHE_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DEPLOY_ON_STARTUP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.config.PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATION_MODE;

/**
 * Utility methods for {@link javax.persistence.EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JpaProperties {

    public static Map<String, Object> from(EntityManagerFactoryConfig config) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put(DDL_GENERATION, config.ddlGeneration());
        jpaProperties.put(DDL_GENERATION_MODE, config.ddlGenerationOutputMode());
        // DEPLOY_ON_STARTUP must be a string value
        jpaProperties.put(DEPLOY_ON_STARTUP, Boolean.toString(config.deployOnStartup()));
        jpaProperties.put(LOGGING_LEVEL, config.loggingLevel());
        jpaProperties.put(TRANSACTION_TYPE, config.persistenceUnitTransactionType());
        jpaProperties.put(ECLIPSELINK_PERSISTENCE_XML, config.persistenceXmlLocation());
        jpaProperties.put(SHARED_CACHE_MODE, config.sharedCacheMode());
        jpaProperties.put(VALIDATION_MODE, config.validationMode());
        jpaProperties.put(PERSISTENCE_PROVIDER, config.persistenceProviderClassName());
        if (config.useExceptionHandler()) {
            jpaProperties.put(EXCEPTION_HANDLER_CLASS, JpaExceptionHandler.class.getName());
        }
        // Extra properties are in [key=value] format.
        jpaProperties.putAll(Stream.of(config.jpaProperties())
                .filter(StringUtils::isNotEmpty)
                .map(row -> row.split(EQ))
                .filter(mapping -> ArrayUtils.getLength(mapping) == 2)
                .collect(Collectors.toMap(elem -> elem[0], elem -> elem[1])));
        return jpaProperties;
    }
}
