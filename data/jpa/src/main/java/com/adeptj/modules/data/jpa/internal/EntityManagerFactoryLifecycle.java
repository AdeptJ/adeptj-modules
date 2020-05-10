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
import com.adeptj.modules.data.jpa.PersistenceInfoProvider;
import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import com.adeptj.modules.data.jpa.exception.JpaBootstrapException;
import com.adeptj.modules.data.jpa.exception.JpaRepositoryBindException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;
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
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Manages {@link EntityManagerFactory}'s lifecycle.
 * <p>
 * Sets the {@link EntityManagerFactory} instance to the {@link JpaRepository} implementation.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
@Designate(ocd = EntityManagerFactoryConfig.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class EntityManagerFactoryLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PU_NAME_NULL_EX_MSG = "PersistenceInfoProvider [%s]#getPersistenceUnitName " +
            "must return a non null unit name!!";

    private static final String EMF_NULL_EX_MSG = "Couldn't create EntityManagerFactory, please check server logs " +
            "for exceptions or probably inspect your persistence.xml for any discrepancies!!";

    private final String unitName;

    private final EntityManagerFactoryWrapper entityManagerFactoryWrapper;

    @Activate
    public EntityManagerFactoryLifecycle(@Reference DataSourceService dataSourceService,
                                         @Reference ValidatorService validatorService,
                                         @NotNull @Reference PersistenceInfoProvider provider,
                                         @NotNull EntityManagerFactoryConfig config) {
        this.unitName = provider.getPersistenceUnitName();
        if (StringUtils.isEmpty(this.unitName)) {
            throw new JpaBootstrapException(String.format(PU_NAME_NULL_EX_MSG, provider));
        }
        try {
            Map<String, Object> properties = JpaProperties.from(config);
            Map<String, Object> providerProperties = provider.getPersistenceUnitProperties();
            if (providerProperties != null && !providerProperties.isEmpty()) {
                properties.putAll(providerProperties);
            }
            properties.put(NON_JTA_DATASOURCE, dataSourceService.getDataSource());
            ValidationMode validationMode = ValidationMode.valueOf(config.validationMode());
            if (validationMode == AUTO || validationMode == CALLBACK) {
                properties.put(VALIDATOR_FACTORY, validatorService.getValidatorFactory());
            }
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", this.unitName);
            // Important Note: The ClassLoader must be the one which loaded the given PersistenceInfoProvider
            // implementation and it must have the visibility to all the entity classes and persistence.xml/orm.xml
            // otherwise EclipseLink may not be able to create the EntityManagerFactory.
            LOGGER.info("Using ClassLoader of PersistenceInfoProvider: [{}]", provider.getClass().getName());
            properties.put(CLASSLOADER, provider.getClass().getClassLoader());
            EntityManagerFactory emf = new PersistenceProvider().createEntityManagerFactory(this.unitName, properties);
            Validate.validState(emf != null, EMF_NULL_EX_MSG);
            this.entityManagerFactoryWrapper = new EntityManagerFactoryWrapper(emf);
            LOGGER.info("Created EntityManagerFactory for PersistenceUnit: [{}]", this.unitName);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Deactivate
    protected void stop(@NotNull EntityManagerFactoryConfig config) {
        LOGGER.info("Closing EntityManagerFactory for PU [{}]", this.unitName);
        this.entityManagerFactoryWrapper.close();
        LOGGER.info("EntityManagerFactory closed!!");
    }

    // <<----------------------------------- JpaRepository Bind ------------------------------------>>

    @Reference(service = JpaRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindJpaRepository(JpaRepository<?, ?> repository) {
        if (!(repository instanceof AbstractJpaRepository)) {
            throw new JpaRepositoryBindException("The repository instance must extend AbstractJpaRepository!");
        }
        LOGGER.info("Binding JpaRepository: {} for persistence unit: [{}]", repository, this.unitName);
        AbstractJpaRepository<?, ?> jpaRepository = (AbstractJpaRepository<?, ?>) repository;
        jpaRepository.setEntityManagerFactory(this.entityManagerFactoryWrapper);
    }

    protected void unbindJpaRepository(JpaRepository<?, ?> repository) {
        // Let's do an explicit type check to avoid a CCE.
        if (repository instanceof AbstractJpaRepository) {
            LOGGER.info("Unbinding JpaRepository: {} for persistence unit: [{}]", repository, this.unitName);
            AbstractJpaRepository<?, ?> jpaRepository = (AbstractJpaRepository<?, ?>) repository;
            jpaRepository.setEntityManagerFactory(null);
        }
    }
}
