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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.adeptj.modules.data.jpa.JpaConstants.JPA_FACTORY_PID;
import static com.adeptj.modules.data.jpa.JpaConstants.PU_NAME;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATOR_FACTORY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

/**
 * Implementation of {@link JpaRepository} based on EclipseLink JPA Reference Implementation
 * <p>
 * This will be registered with the OSGi service registry whenever there is a new EntityManagerFactory configuration
 * created from OSGi console.
 * <p>
 * Therefore there will be a separate service for each PersistenceUnit.
 * <p>
 * Callers will have to provide an OSGi filter while injecting a reference of {@link JpaRepository}
 *
 * <code>
 * &#064;Reference(target="(osgi.unit.name=my_persistence_unit)")
 * private JpaRepository repository;
 * </code>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class, factory = true)
@Component(immediate = true, name = JPA_FACTORY_PID, configurationPolicy = REQUIRE)
public class JpaRepositoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConcurrentMap<String, JpaRepository> repositories = new ConcurrentHashMap<>();

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;

    // <---------------------------------------------- OSGi Internal ------------------------------------------------>

    @Activate
    protected void start(EntityManagerFactoryConfig config) {
        try {
            Map<String, Object> jpaProperties = JpaProperties.from(config);
            //jpaProperties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(config.dataSourceName()));
            jpaProperties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
            this.repositories.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(config.persistenceUnitName()))
                    .filter(entry -> entry.getValue() instanceof AbstractJpaRepository)
                    .map(entry -> (AbstractJpaRepository) entry.getValue())
                    .findFirst()
                    .ifPresent(repository -> {
                        EntityManagerFactory emf = repository.createEntityManagerFactory(jpaProperties);
                        JpaUtil.assertInitialized(emf);
                        repository.setEntityManagerFactory(emf);
                    });
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
        String unitName = config.persistenceUnitName();
        this.repositories.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(unitName))
                .filter(entry -> entry.getValue() instanceof AbstractJpaRepository)
                .map(entry -> (AbstractJpaRepository) entry.getValue())
                .filter(AbstractJpaRepository::isEntityManagerFactoryInitialized)
                .findFirst()
                .ifPresent(repository -> {
                    LOGGER.info("Closing EntityManagerFactory for PU [{}]", unitName);
                    repository.closeEntityManagerFactory();
                    repository.setEntityManagerFactory(null);
                });
        this.repositories.remove(unitName);
    }

    @Reference(service = JpaRepository.class, cardinality = MULTIPLE, policy = DYNAMIC, policyOption = GREEDY)
    protected void bindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        if (repository instanceof AbstractJpaRepository) {
            String unitName = (String) properties.get(PU_NAME);
            LOGGER.info("Binding JpaRepository for PU [{}]", unitName);
            this.repositories.put(unitName, repository);
        }
    }

    protected void unbindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        String unitName = (String) properties.get(PU_NAME);
        LOGGER.info("Unbinding JpaRepository for PU [{}]", unitName);
        this.repositories.remove(unitName);
        if (repository instanceof AbstractJpaRepository) {
            AbstractJpaRepository jpaRepository = (AbstractJpaRepository) repository;
            if (jpaRepository.isEntityManagerFactoryInitialized()) {
                LOGGER.info("Closing EntityManagerFactory for PU [{}]", unitName);
                jpaRepository.closeEntityManagerFactory();
                jpaRepository.setEntityManagerFactory(null);
            }
        }
    }
}
