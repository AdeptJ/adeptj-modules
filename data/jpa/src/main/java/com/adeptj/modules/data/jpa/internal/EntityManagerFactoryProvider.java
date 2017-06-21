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

import com.adeptj.modules.commons.ds.api.DataSourceProvider;
import com.adeptj.modules.data.jpa.JpaExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.adeptj.modules.data.jpa.JpaConstants.PERSISTENCE_PROVIDER;
import static com.adeptj.modules.data.jpa.JpaConstants.SHARED_CACHE_MODE;
import static com.adeptj.modules.data.jpa.internal.EntityManagerFactoryProvider.NAME;
import static com.adeptj.modules.data.jpa.internal.EntityManagerFactoryProvider.SERVICE_PID;
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
 * Provides an instance of {@link javax.persistence.EntityManagerFactory} from configured factories.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class)
@Component(immediate = true, name = NAME, property = SERVICE_PID, configurationPolicy = ConfigurationPolicy.IGNORE)
public class EntityManagerFactoryProvider implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    static final String NAME = "com.adeptj.modules.data.jpa.EntityManagerFactoryProvider.factory";

    static final String SERVICE_PID = "service.pid=com.adeptj.modules.data.jpa.EntityManagerFactoryProvider.factory";

    private Map<String, EntityManagerFactory> unitNameToEMFMapping = new ConcurrentHashMap<>();

    private Map<String, String> pidToUnitNameMapping = new ConcurrentHashMap<>();

    @Reference
    private JpaCrudRepositoryManager repositoryManager;

    @Reference
    private DataSourceProvider dsProvider;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        // If there is an update to existing PID, removes all the mappings against that PID.
        // Close corresponding EntityManagerFactory as well.
        this.handleUpdateDelete(pid);
        // Recreate the EntityManagerFactory.
        this.createEntityManagerFactory(pid, properties);
    }

    @Override
    public void deleted(String pid) {
        this.handleUpdateDelete(pid);
    }

    private void handleUpdateDelete(String pid) {
        Optional.ofNullable(this.pidToUnitNameMapping.remove(pid)).ifPresent(unitName -> {
            this.repositoryManager.unregisterJpaCrudRepository(unitName);
            LOGGER.info("Closing EntityManagerFactory against PersistenceUnit: [{}]", unitName);
            this.unitNameToEMFMapping.remove(unitName).close();
        });
    }

    private void createEntityManagerFactory(String pid, Dictionary<String, ?> configs) {
        Map<String, Object> jpaProperties = new HashMap<>();
        String dataSourceName = (String) configs.get("dataSourceName");
        Objects.requireNonNull(dataSourceName, "dataSourceName cannot be null!!");
        DataSource dataSource = this.dsProvider.getDataSource(dataSourceName);
        Objects.requireNonNull(dataSource, "DataSource cannot be null!!");
        jpaProperties.put(NON_JTA_DATASOURCE, dataSource);
        jpaProperties.put(DDL_GENERATION, (String) configs.get("ddlGeneration"));
        jpaProperties.put(DDL_GENERATION_MODE, (String) configs.get("ddlGenerationMode"));
        jpaProperties.put(DEPLOY_ON_STARTUP, Boolean.toString((Boolean) configs.get("deployOnStartup")));
        jpaProperties.put(LOGGING_FILE, (String) configs.get("loggingFile"));
        jpaProperties.put(LOGGING_LEVEL, (String) configs.get("loggingLevel"));
        jpaProperties.put(TRANSACTION_TYPE, (String) configs.get("persistenceUnitTransactionType"));
        jpaProperties.put(ECLIPSELINK_PERSISTENCE_XML, (String) configs.get("persistenceXmlLocation"));
        jpaProperties.put(SHARED_CACHE_MODE, (String) configs.get("sharedCacheMode"));
        jpaProperties.put(PERSISTENCE_PROVIDER, (String) configs.get("persistenceProviderClassName"));
        jpaProperties.put(EXCEPTION_HANDLER_CLASS, JpaExceptionHandler.class.getName());
        jpaProperties.put(CLASSLOADER, this.getClass().getClassLoader());
        jpaProperties.put(VALIDATION_MODE, (String) configs.get("validationMode"));
        jpaProperties.putAll(Arrays.stream((String[]) configs.get("jpaProperties")).filter(StringUtils::isNotBlank)
                .map(prop -> prop.split("=")).collect(Collectors.toMap(elem -> elem[0], elem -> elem[1])));
        try {
            String unitName = (String) configs.get("unitName");
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", unitName);
            EntityManagerFactory emf = new PersistenceProvider().createEntityManagerFactory(unitName, jpaProperties);
            if (emf == null) {
                LOGGER.warn("Could not initialize EntityManagerFactory, Please check configurations!!");
            } else {
                LOGGER.info("EntityManagerFactory [{}] created for PersistenceUnit: [{}]", emf, unitName);
                this.pidToUnitNameMapping.put(pid, unitName);
                this.unitNameToEMFMapping.put(unitName, emf);
                this.repositoryManager.registerJpaCrudRepository(unitName, emf);
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception occurred while creating EntityManagerFactory!!", ex);
        }
    }
}
