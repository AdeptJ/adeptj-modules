/*
 * =============================================================================
 *
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * =============================================================================
 */
package com.adeptj.modules.data.jpa.impl;

import com.adeptj.modules.data.jpa.EntityManagerProvider;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CLASSLOADER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NON_JTA_DATASOURCE;

/**
 * Implementation for JPA Entity manager.
 *
 * @author prince.arora, AdeptJ
 */
@Designate(ocd = EntityManagerConfig.class)
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class JPAEntityManagerProvider implements EntityManagerProvider {

    private static final Logger log = LoggerFactory.getLogger(JPAEntityManagerProvider.class);

    @Reference
    private DataSourceFactory sourceFactory;

    private EntityManagerFactory managerFactory;

    private String unitName;

    /**
     * Entity Manager factory to populate entity manager.
     *
     * @return
     * @throws SQLException
     */
    public EntityManagerFactory getEntityManagerFactory() throws SQLException {
        if (this.managerFactory == null) {
            Map<String, Object> properties = new HashMap<>();
            properties.put(NON_JTA_DATASOURCE, this.sourceFactory.createDataSource(null));
            properties.put(CLASSLOADER, this.getClass().getClassLoader());
            PersistenceProvider provider = new PersistenceProvider();
            this.managerFactory = provider.createEntityManagerFactory(this.unitName, properties);
        }
        log.info("Entitry manager factory: "+ this.managerFactory);
        return this.managerFactory;
    }

    @Activate
    protected void activate(ComponentContext context) {
        this.unitName = (String) context.getProperties().get("persistenceUnitName");
        try {
            this.getEntityManagerFactory();
            for (EntityType<?> entityType : this.managerFactory.getMetamodel().getEntities()) {
                log.info("Reserved Entity Type: "+ entityType.getName());
            }
        } catch (SQLException ex) {
            log.error("Unable to initialize entity manager factory: ", ex);
        }
    }

    @Override
    public EntityManager getEntityManager() throws SQLException {
        if (this.managerFactory == null) {
            this.getEntityManagerFactory();
        }
        return this.managerFactory.createEntityManager();
    }
}
