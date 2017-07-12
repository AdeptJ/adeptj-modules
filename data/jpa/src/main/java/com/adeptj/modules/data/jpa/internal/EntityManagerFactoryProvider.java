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
import static com.adeptj.modules.data.jpa.internal.EntityManagerFactoryProvider.FACTORY_NAME;
import static com.adeptj.modules.data.jpa.internal.EntityManagerFactoryProvider.SERVICE_PID_PROPERTY;
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
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * Provides an instance of {@link javax.persistence.EntityManagerFactory} from configured factories.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class)
@Component(immediate = true, name = FACTORY_NAME, property = SERVICE_PID_PROPERTY, configurationPolicy = IGNORE)
public class EntityManagerFactoryProvider implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    static final String FACTORY_NAME = "com.adeptj.modules.data.jpa.EntityManagerFactoryProvider.factory";

    static final String SERVICE_PID_PROPERTY = "service.pid=com.adeptj.modules.data.jpa.EntityManagerFactoryProvider.factory";

    private Map<String, EntityManagerFactory> unitNameVsEMFMapping = new ConcurrentHashMap<>();

    private Map<String, String> pidVsUnitNameMapping = new ConcurrentHashMap<>();

    @Reference
    private JpaCrudRepositoryManager repositoryManager;

    @Reference
    private DataSourceProvider dsProvider;

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        // If there is an update to existing PID, remove the mapping against that PID.
        // Close corresponding EntityManagerFactory as well.
        this.handleConfigChange(pid);
        // Recreate the EntityManagerFactory.
        this.createEntityManagerFactory(pid, properties);
    }

    @Override
    public void deleted(String pid) {
        this.handleConfigChange(pid);
    }

    private void handleConfigChange(String pid) {
        Optional.ofNullable(this.pidVsUnitNameMapping.remove(pid)).ifPresent(unitName -> {
            this.repositoryManager.unregisterJpaCrudRepository(unitName);
            LOGGER.info("Closing EntityManagerFactory against PersistenceUnit: [{}]", unitName);
            this.unitNameVsEMFMapping.remove(unitName).close();
        });
    }

    private void createEntityManagerFactory(String pid, Dictionary<String, ?> configs) {
        try {
            String unitName = (String) configs.get("unitName");
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", unitName);
            EntityManagerFactory emf = new PersistenceProvider().createEntityManagerFactory(unitName, this.jpaProperties(configs));
            if (emf == null) {
                LOGGER.warn("Could not initialize EntityManagerFactory, Most probably persistence.xml not found!!");
            } else {
                LOGGER.info("EntityManagerFactory [{}] created for PersistenceUnit: [{}]", emf, unitName);
                this.pidVsUnitNameMapping.put(pid, unitName);
                this.unitNameVsEMFMapping.put(unitName, emf);
                this.repositoryManager.registerJpaCrudRepository(unitName, emf);
                if (LOGGER.isDebugEnabled()) {
                    emf.getMetamodel().getEntities().forEach(type -> LOGGER.debug("EntityType: [{}]", type.getName()));
                }
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception occurred while creating EntityManagerFactory!!", ex);
        }
    }

    private Map<String, Object> jpaProperties(Dictionary<String, ?> configs) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put(NON_JTA_DATASOURCE, this.getDataSource(configs));
        jpaProperties.put(DDL_GENERATION, configs.get("ddlGeneration"));
        jpaProperties.put(DDL_GENERATION_MODE, configs.get("ddlGenerationOutputMode"));
        jpaProperties.put(DEPLOY_ON_STARTUP, Boolean.toString((Boolean) configs.get("deployOnStartup")));
        jpaProperties.put(LOGGING_FILE, configs.get("loggingFile"));
        jpaProperties.put(LOGGING_LEVEL, configs.get("loggingLevel"));
        jpaProperties.put(TRANSACTION_TYPE, configs.get("persistenceUnitTransactionType"));
        jpaProperties.put(ECLIPSELINK_PERSISTENCE_XML, configs.get("persistenceXmlLocation"));
        jpaProperties.put(SHARED_CACHE_MODE, configs.get("sharedCacheMode"));
        jpaProperties.put(PERSISTENCE_PROVIDER, configs.get("persistenceProviderClassName"));
        jpaProperties.put(EXCEPTION_HANDLER_CLASS, JpaExceptionHandler.class.getName());
        jpaProperties.put(CLASSLOADER, this.getClass().getClassLoader());
        jpaProperties.put(VALIDATION_MODE, configs.get("validationMode"));
        // Extra properties are in [key=value] format, maximum of 100 properties can be provided.
        jpaProperties.putAll(Arrays.stream((String[]) configs.get("jpaProperties"))
                .filter(StringUtils::isNotBlank)
                .map(prop -> prop.split("="))
                .collect(Collectors.toMap(elem -> elem[0], elem -> elem[1])));
        return jpaProperties;
    }

    private DataSource getDataSource(Dictionary<String, ?> configs) {
        return Objects.requireNonNull(this.dsProvider.getDataSource((String) configs.get("dataSourceName")),
                "DataSource cannot be null!!");
    }
}
