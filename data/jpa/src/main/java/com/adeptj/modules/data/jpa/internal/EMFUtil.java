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
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.adeptj.modules.data.jpa.JpaConstants.PERSISTENCE_PROVIDER;
import static com.adeptj.modules.data.jpa.JpaConstants.SHARED_CACHE_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CLASSLOADER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DEPLOY_ON_STARTUP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.config.PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_FILE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NON_JTA_DATASOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATION_MODE;

/**
 * Utility methods for {@link javax.persistence.EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class EMFUtil {

    static Map<String, Object> createJpaProperties(DataSource dataSource, Dictionary<String, ?> configs) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put(NON_JTA_DATASOURCE, dataSource);
        jpaProperties.put(DDL_GENERATION, configs.get("ddlGeneration"));
        jpaProperties.put(DDL_GENERATION_MODE, configs.get("ddlGenerationOutputMode"));
        jpaProperties.put(DEPLOY_ON_STARTUP, Boolean.toString((Boolean) configs.get("deployOnStartup")));
        jpaProperties.put(LOGGING_FILE, configs.get("loggingFile"));
        jpaProperties.put(LOGGING_LEVEL, configs.get("loggingLevel"));
        jpaProperties.put(TRANSACTION_TYPE, configs.get("persistenceUnitTransactionType"));
        jpaProperties.put(ECLIPSELINK_PERSISTENCE_XML, configs.get("persistenceXmlLocation"));
        jpaProperties.put(SHARED_CACHE_MODE, configs.get("sharedCacheMode"));
        jpaProperties.put(PERSISTENCE_PROVIDER, configs.get("persistenceProviderClassName"));
        if ((Boolean) configs.get("useExceptionHandler")) {
            jpaProperties.put(EXCEPTION_HANDLER_CLASS, JpaExceptionHandler.class.getName());
        }
        jpaProperties.put(CLASSLOADER, EntityManagerFactoryProvider.class.getClassLoader());
        jpaProperties.put(VALIDATION_MODE, configs.get("validationMode"));
        // Extra properties are in [key=value] format, maximum of 100 properties can be provided.
        jpaProperties.putAll(Arrays.stream((String[]) configs.get("jpaProperties"))
                .filter(StringUtils::isNotEmpty)
                .map(prop -> prop.split("="))
                .collect(Collectors.toMap(elem -> elem[0], elem -> elem[1])));
        return jpaProperties;
    }
}
