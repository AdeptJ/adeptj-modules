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
import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import com.adeptj.modules.data.jpa.core.EntityManagerFactoryConfig;
import com.adeptj.modules.data.jpa.core.JpaProperties;
import com.adeptj.modules.data.jpa.exception.JpaBootstrapException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ValidationMode;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.data.jpa.JpaConstants.JPA_FACTORY_PID;
import static javax.persistence.ValidationMode.AUTO;
import static javax.persistence.ValidationMode.CALLBACK;
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
@Component(immediate = true, name = JPA_FACTORY_PID, configurationPolicy = REQUIRE)
public class JpaRepositoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<JpaRepositoryWrapper> repositoryWrappers = new CopyOnWriteArrayList<>();

    private Map<String, Object> jpaProperties;

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;

    // <------------------------------------- JpaRepositoryFactory Lifecycle -------------------------------------->

    @Activate
    public void start(EntityManagerFactoryConfig config) {
        String persistenceUnit = config.persistenceUnit();
        try {
            Validate.isTrue(StringUtils.isNotEmpty(persistenceUnit), "PersistenceUnit name can't be blank!!");
            this.jpaProperties = JpaProperties.from(config);
            this.repositoryWrappers.stream()
                    .filter(wrapper -> StringUtils.equals(wrapper.getPersistenceUnit(), persistenceUnit))
                    .forEach(wrapper -> {
                        this.jpaProperties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(config.dataSourceName()));
                        ValidationMode validationMode = ValidationMode.valueOf(config.validationMode());
                        if (validationMode == AUTO || validationMode == CALLBACK) {
                            this.jpaProperties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
                        }
                        LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
                        wrapper.initEntityManagerFactory(this.jpaProperties);
                        LOGGER.info("Created EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
                    });
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    @Deactivate
    public void stop(EntityManagerFactoryConfig config) {
        String persistenceUnit = config.persistenceUnit();
        if (this.jpaProperties != null) {
            this.jpaProperties.clear();
            this.jpaProperties = null;
        }
        this.repositoryWrappers.stream()
                .filter(wrapper -> StringUtils.equals(wrapper.getPersistenceUnit(), persistenceUnit))
                .forEach(wrapper -> {
                    LOGGER.info("Closing EntityManagerFactory for PU [{}]", persistenceUnit);
                    wrapper.disposeRepository();
                });
        this.repositoryWrappers.removeIf(rw -> StringUtils.equals(rw.getPersistenceUnit(), persistenceUnit));
    }

    // <------------------------------------- JpaRepository Lifecycle Listener -------------------------------------->

    @Reference(service = JpaRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    public void bindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        String persistenceUnit = (String) properties.get(JPA_UNIT_NAME);
        if (StringUtils.isEmpty(persistenceUnit)) {
            LOGGER.warn("{} must specify the [{}] service property!!", repository, JPA_UNIT_NAME);
            return;
        }
        LOGGER.info("Binding JpaRepository for PU [{}]", persistenceUnit);
        JpaRepositoryWrapper wrapper = new JpaRepositoryWrapper(persistenceUnit, repository);
        if (this.jpaProperties != null) {
            this.jpaProperties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(""));
            ValidationMode validationMode = ValidationMode.valueOf("");
            if (validationMode == AUTO || validationMode == CALLBACK) {
                this.jpaProperties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
            }
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
            wrapper.initEntityManagerFactory(this.jpaProperties);
            LOGGER.info("Created EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
        }
        this.repositoryWrappers.add(wrapper);
    }

    public void unbindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        String persistenceUnit = (String) properties.get(JPA_UNIT_NAME);
        LOGGER.info("Unbinding JpaRepository for PU [{}]", persistenceUnit);
        this.repositoryWrappers.stream()
                .filter(wrapper -> StringUtils.equals(wrapper.getPersistenceUnit(), persistenceUnit))
                .forEach(wrapper -> {
                    LOGGER.info("Closing EntityManagerFactory for PU [{}]", persistenceUnit);
                    wrapper.disposeRepository();
                });
        this.repositoryWrappers.removeIf(rw -> StringUtils.equals(rw.getPersistenceUnit(), persistenceUnit));
        ((AbstractJpaRepository) repository).setEntityManagerFactory(null);
    }
}
