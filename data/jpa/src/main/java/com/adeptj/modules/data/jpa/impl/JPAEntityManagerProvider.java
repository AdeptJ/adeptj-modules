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

import com.adeptj.modules.data.jpa.EntityManagerConfig;
import com.adeptj.modules.data.jpa.EntityManagerProvider;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CLASSLOADER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_OR_EXTEND;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_BOTH_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DEPLOY_ON_STARTUP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_FILE;
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
    private DataSourceFactory dsFactory;

    private volatile EntityManagerFactory emf;

    private String unitName;

    @Override
    public EntityManager getEntityManager() {
        return this.emf.createEntityManager();
    }

    // Lifecycle Methods.

    @Activate
    protected void activate(EntityManagerConfig config) {
        this.unitName = config.persistenceUnitName();
        this.createEMF();
        this.emf.getMetamodel().getEntities().forEach(entityType -> log.info("Reserved Entity Type: {}", entityType.getName()));
    }

    @Deactivate
    protected void deactivate() {
        Optional.ofNullable(this.emf).ifPresent(emfConsumer -> this.emf.close());
    }

    private void createEMF() {
        this.emf = Optional.ofNullable(this.emf).orElseGet(() -> {
            EntityManagerFactory entityManagerFactory = null;
            Map<String, Object> jpaProperties = new HashMap<>();
            try {
                jpaProperties.put(NON_JTA_DATASOURCE, this.dsFactory.createDataSource(null));
                jpaProperties.put(DDL_GENERATION, CREATE_OR_EXTEND);
                jpaProperties.put(DDL_GENERATION_MODE, DDL_BOTH_GENERATION);
                jpaProperties.put(DEPLOY_ON_STARTUP, "true");
                jpaProperties.put(LOGGING_FILE, "jpa.log");
                jpaProperties.put(CLASSLOADER, this.getClass().getClassLoader());
                PersistenceProvider provider = new PersistenceProvider();
                entityManagerFactory = provider.createEntityManagerFactory(this.unitName, jpaProperties);
            } catch (Exception ex) { // NOSONAR
                log.error("Exception occurred!!", ex);
            }
            return entityManagerFactory;
        });
        log.info("EntityManagerFactory: {}", this.emf);
    }
}
