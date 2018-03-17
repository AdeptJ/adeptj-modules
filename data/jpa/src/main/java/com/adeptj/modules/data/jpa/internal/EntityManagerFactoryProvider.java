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
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.adeptj.modules.data.jpa.internal.EntityManagerFactoryProvider.COMPONENT_NAME;
import static java.util.Objects.requireNonNull;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * Provides an instance of {@link javax.persistence.EntityManagerFactory} from configured factories.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class EntityManagerFactoryProvider implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.data.jpa.EntityManagerFactoryProvider.factory";

    private static final String FACTORY_NAME = "AdeptJ EntityManagerFactoryProvider";

    private Map<String, EntityManagerFactory> unitNameVsEMFMapping = new ConcurrentHashMap<>();

    private Map<String, String> pidVsUnitNameMapping = new ConcurrentHashMap<>();

    @Reference
    private JpaCrudRepositoryManager repositoryManager;

    @Reference
    private DataSourceProvider dataSourceProvider;

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
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
        EntityManagerFactory emf = null;
        try {
            String unitName = (String) configs.get("unitName");
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", unitName);
            DataSource dataSource = requireNonNull(this.dataSourceProvider.getDataSource((String) configs.get("dataSourceName")),
                    "DataSource cannot be null!!");
            emf = new PersistenceProvider().createEntityManagerFactory(unitName, EMFUtil.createJpaProperties(dataSource, configs));
            if (emf == null) {
                LOGGER.warn("Could not initialize EntityManagerFactory, Most probably persistence.xml not found!!");
            } else {
                LOGGER.info("EntityManagerFactory [{}] created for PersistenceUnit: [{}]", emf, unitName);
                this.pidVsUnitNameMapping.put(pid, unitName);
                this.unitNameVsEMFMapping.put(unitName, emf);
                this.repositoryManager.registerJpaCrudRepository(unitName, emf);
                if (LOGGER.isDebugEnabled()) {
                    emf.getMetamodel()
                            .getEntities()
                            .forEach(type -> LOGGER.debug("EntityType: [{}]", type.getName()));
                }
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception occurred while creating EntityManagerFactory!!", ex);
            if (emf != null) {
                emf.close();
            }
        }
    }
}
