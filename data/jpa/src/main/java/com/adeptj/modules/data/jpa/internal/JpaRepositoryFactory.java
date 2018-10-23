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

import com.adeptj.modules.commons.jdbc.DataSourceService;
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.JpaUtil;
import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import com.adeptj.modules.data.jpa.core.EntityManagerFactoryConfig;
import com.adeptj.modules.data.jpa.core.JpaProperties;
import com.adeptj.modules.data.jpa.exception.JpaBootstrapException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.data.jpa.JpaConstants.JPA_FACTORY_PID;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CLASSLOADER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NON_JTA_DATASOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATOR_FACTORY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.jpa.EntityManagerFactoryBuilder.JPA_UNIT_NAME;

/**
 * JpaRepositoryFactory.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class, factory = true)
@Component(name = JPA_FACTORY_PID, configurationPolicy = REQUIRE)
public class JpaRepositoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<JpaRepositoryWrapper> repositories = new CopyOnWriteArrayList<>();

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;

    // <------------------------------------- JpaRepositoryFactory Lifecycle -------------------------------------->

    @Activate
    protected void start(EntityManagerFactoryConfig config) {
        String persistenceUnitName = config.persistenceUnitName();
        try {
            Validate.isTrue(StringUtils.isNotEmpty(persistenceUnitName), "PersistenceUnit name can't be blank!!");
            Map<String, Object> properties = JpaProperties.from(config);
            properties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(config.dataSourceName()));
            properties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
            this.repositories.stream()
                    .filter(rw -> StringUtils.equals(rw.getPersistenceUnitName(), persistenceUnitName))
                    .findFirst()
                    .ifPresent(rw -> {
                        JpaRepository repository = rw.getRepository();
                        properties.put(CLASSLOADER, repository.getClass().getClassLoader());
                        LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnitName);
                        PersistenceProvider provider = new PersistenceProvider();
                        EntityManagerFactory emf = provider.createEntityManagerFactory(persistenceUnitName, properties);
                        JpaUtil.assertInitialized(emf);
                        rw.setEntityManagerFactory(emf);
                        LOGGER.info("Created EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnitName);
                    });
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
        String persistenceUnitName = config.persistenceUnitName();
        this.repositories.stream()
                .filter(rw -> StringUtils.equals(rw.getPersistenceUnitName(), persistenceUnitName))
                .findFirst()
                .ifPresent(rw -> {
                    LOGGER.info("Closing EntityManagerFactory for PU [{}]", persistenceUnitName);
                    JpaUtil.close(rw.getEntityManagerFactory());
                    rw.setEntityManagerFactory(null);
                    rw.unsetRepository();
                });
    }

    // <------------------------------------- JpaRepository Lifecycle Listener -------------------------------------->

    @Reference(service = JpaRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        String persistenceUnitName = (String) properties.get(JPA_UNIT_NAME);
        if (StringUtils.isEmpty(persistenceUnitName)) {
            LOGGER.warn("{} must specify the [{}] service property!!", repository, JPA_UNIT_NAME);
            return;
        }
        LOGGER.info("Binding JpaRepository for PU [{}]", persistenceUnitName);
        this.repositories.add(new JpaRepositoryWrapper(persistenceUnitName, repository));
    }

    protected void unbindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        String persistenceUnitName = (String) properties.get(JPA_UNIT_NAME);
        LOGGER.info("Unbinding JpaRepository for PU [{}]", persistenceUnitName);
        LOGGER.info("Closing EntityManagerFactory for PU [{}]", persistenceUnitName);
        this.repositories.stream()
                .filter(rw -> StringUtils.equals(rw.getPersistenceUnitName(), persistenceUnitName))
                .findFirst()
                .ifPresent(rw -> {
                    JpaUtil.close(rw.getEntityManagerFactory());
                    rw.setEntityManagerFactory(null);
                    rw.unsetRepository();
                });
        this.repositories.removeIf(rw -> StringUtils.equals(rw.getPersistenceUnitName(), persistenceUnitName));
        ((AbstractJpaRepository) repository).setEntityManagerFactory(null);
    }
}
