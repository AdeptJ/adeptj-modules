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

package com.adeptj.modules.data.jpa.eclipselink.internal;

import com.adeptj.modules.commons.jdbc.DataSourceService;
import com.adeptj.modules.commons.utils.MapUtil;
import com.adeptj.modules.commons.utils.TimeUtil;
import com.adeptj.modules.commons.validator.ValidatorService;
import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.PersistenceInfoProvider;
import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import com.adeptj.modules.data.jpa.util.JpaUtil;
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

    private static final String EMF_CREATED_MSG = "Created EntityManagerFactory for PersistenceUnit: [{}] in [{}] ms!!";

    private static final String EMF_CLOSED_MSG = "EntityManagerFactory closed for PersistenceUnit [{}] in [{}] ms!!";

    private final String unitName;

    private final EntityManagerFactory entityManagerFactory;

    /**
     * Initializes the {@link EntityManagerFactory} with necessary configurations.
     * <p>
     * Important Note: For creating the {@link EntityManagerFactory} the {@link ClassLoader} of the
     * {@link PersistenceInfoProvider} service will be used, and it must have the visibility to all the entity classes,
     * persistence.xml and mapping files such as orm.xml otherwise EclipseLink may not be able to create
     * the {@link EntityManagerFactory}
     *
     * @param dataSourceService for providing a non JTA JDBC DataSource.
     * @param validatorService  for providing the bean ValidatorFactory.
     * @param provider          for providing the persistence unit name and other properties.
     * @param config            for providing the EntityManagerFactory configurations.
     */
    @Activate
    public EntityManagerFactoryLifecycle(@NotNull @Reference DataSourceService dataSourceService,
                                         @NotNull @Reference ValidatorService validatorService,
                                         @NotNull @Reference PersistenceInfoProvider provider,
                                         @NotNull EntityManagerFactoryConfig config) {
        long startTime = System.nanoTime();
        this.unitName = provider.getPersistenceUnitName();
        if (StringUtils.isEmpty(this.unitName)) {
            throw new JpaBootstrapException(String.format(PU_NAME_NULL_EX_MSG, provider));
        }
        LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", this.unitName);
        try {
            Map<String, Object> properties = JpaProperties.create(config);
            Map<String, Object> providerProperties = provider.getPersistenceUnitProperties();
            if (MapUtil.isNotEmpty(providerProperties)) {
                properties.putAll(providerProperties);
            }
            properties.put(NON_JTA_DATASOURCE, dataSourceService.getDataSource());
            ValidationMode validationMode = ValidationMode.valueOf(config.validation_mode());
            if (validationMode == AUTO || validationMode == CALLBACK) {
                properties.put(VALIDATOR_FACTORY, validatorService.getValidatorFactory());
            }
            properties.put(CLASSLOADER, provider.getClass().getClassLoader());
            this.entityManagerFactory = new PersistenceProvider().createEntityManagerFactory(this.unitName, properties);
            Validate.validState((this.entityManagerFactory != null), EMF_NULL_EX_MSG);
            LOGGER.info(EMF_CREATED_MSG, this.unitName, TimeUtil.elapsedMillis(startTime));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    // <<------------------------------------------ OSGi Internal  ------------------------------------------->>

    @Deactivate
    protected void stop() {
        long startTime = System.nanoTime();
        JpaUtil.closeEntityManagerFactory(this.entityManagerFactory);
        // This is needed because the fragment bundle [adeptj-modules-data-jpa-eclipselink-extension] update will
        // fire bundle wiring update and this component will be disposed of, at this moment AbstractSessionLog is still
        // holding the SLF4JLogger instance as a static field and that can cause a memory leak due to the holding of
        // old class and instance. When this component is disposed of due to normal configurations or bundle  update
        // then also remove the SLF4JLogger instance from AbstractSessionLog, because a new one will be created when
        // this component is being activated again and EclipseLink is going to create a new EntityManagerFactory.
        JpaActivator.disposeEclipseLinkSingletonLog();
        LOGGER.info(EMF_CLOSED_MSG, this.unitName, TimeUtil.elapsedMillis(startTime));
    }

    // <<------------------------------------------ JpaRepository Bind ------------------------------------------->>

    @Reference(service = JpaRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindJpaRepository(JpaRepository<?, ?> repository) {
        if (!(repository instanceof AbstractJpaRepository)) {
            throw new JpaRepositoryBindException("The repository instance must extend AbstractJpaRepository!");
        }
        LOGGER.info("Binding JpaRepository: {} to persistence unit: [{}]", repository, this.unitName);
        AbstractJpaRepository<?, ?> jpaRepository = (AbstractJpaRepository<?, ?>) repository;
        jpaRepository.setEntityManagerFactory(new EntityManagerFactoryWrapper(this.entityManagerFactory));
    }

    protected void unbindJpaRepository(JpaRepository<?, ?> repository) {
        // Let's do an explicit type check to avoid a CCE.
        if (repository instanceof AbstractJpaRepository) {
            LOGGER.info("Unbinding JpaRepository: {} from persistence unit: [{}]", repository, this.unitName);
            AbstractJpaRepository<?, ?> jpaRepository = (AbstractJpaRepository<?, ?>) repository;
            jpaRepository.setEntityManagerFactory(null);
        }
    }
}
