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

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.JpaUtil;
import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import com.adeptj.modules.data.jpa.core.EntityManagerFactoryConfig;
import com.adeptj.modules.data.jpa.core.JpaProperties;
import com.adeptj.modules.data.jpa.exception.JpaBootstrapException;
import com.adeptj.modules.data.jpa.exception.JpaRepositoryBindException;
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
import javax.persistence.ValidationMode;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import static javax.persistence.ValidationMode.AUTO;
import static javax.persistence.ValidationMode.CALLBACK;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CLASSLOADER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NON_JTA_DATASOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATOR_FACTORY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.jpa.EntityManagerFactoryBuilder.JPA_UNIT_NAME;

/**
 * Manages {@link javax.persistence.EntityManagerFactory} and sets it to the {@link JpaRepository} implementation.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class)
@Component(service = EntityManagerFactoryLifecycle.class, immediate = true, configurationPolicy = REQUIRE)
public class EntityManagerFactoryLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EMF_NULL_EXCEPTION_MSG = "EntityManagerFactory is null, probably due to missing persistence.xml!!";

    private static final String PU_NOT_MATCHED_EXCEPTION_MSG = "JpaRepository [%s]'s service property [%s] must be equal to " +
            "EntityManagerFactoryConfig#persistenceUnit!!";

    private String repositoryPersistenceUnit;

    private EntityManagerFactory entityManagerFactory;

    private AbstractJpaRepository repository;

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Activate
    protected void start(EntityManagerFactoryConfig config) {
        Validate.validState(this.repository != null, "JpaRepository must not be null!!");
        String persistenceUnit = config.persistenceUnit();
        Validate.isTrue(StringUtils.isNotEmpty(persistenceUnit), "PersistenceUnit name can't be empty!!");
        Validate.validState(StringUtils.equals(this.repositoryPersistenceUnit, persistenceUnit),
                String.format(PU_NOT_MATCHED_EXCEPTION_MSG, this.repository, JPA_UNIT_NAME));
        try {
            Map<String, Object> properties = JpaProperties.from(config);
            properties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(config.dataSourceName()));
            ValidationMode validationMode = ValidationMode.valueOf(config.validationMode());
            if (validationMode == AUTO || validationMode == CALLBACK) {
                properties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
            }
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
            // Note: The ClassLoader must be the one which loaded the given JpaRepository implementation
            // and it must have the visibility to the entity classes and persistence.xml/orm.xml
            // otherwise EclipseLink may not be able to create the EntityManagerFactory.
            properties.put(CLASSLOADER, this.repository.getClass().getClassLoader());
            this.entityManagerFactory = new PersistenceProvider().createEntityManagerFactory(persistenceUnit, properties);
            Validate.validState(this.entityManagerFactory != null, EMF_NULL_EXCEPTION_MSG);
            this.repository.setEntityManagerFactory(this.entityManagerFactory);
            LOGGER.info("Created EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            JpaUtil.closeEntityManagerFactory(this.entityManagerFactory);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
        try {
            this.repository.onClose();
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
        JpaUtil.closeEntityManagerFactory(this.entityManagerFactory);
        this.entityManagerFactory = null;
        this.repository.setEntityManagerFactory(null);
        LOGGER.info("Closed EntityManagerFactory for PU [{}]", config.persistenceUnit());
    }

    // <<----------------------------------- JpaRepository Bind ------------------------------------>>

    @Reference(service = JpaRepository.class)
    protected void bindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        this.repositoryPersistenceUnit = (String) properties.get(JPA_UNIT_NAME);
        try {
            Validate.isTrue(StringUtils.isNotEmpty(this.repositoryPersistenceUnit),
                    String.format("%s must specify the [%s] service property!!", repository, JPA_UNIT_NAME));
            LOGGER.info("Binding JpaRepository for PU [{}]", this.repositoryPersistenceUnit);
            // Not doing any type check purposely, the JpaRepository must be a subclass of AbstractJpaRepository.
            this.repository = (AbstractJpaRepository) repository;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new JpaRepositoryBindException(ex);
        }
    }
}
