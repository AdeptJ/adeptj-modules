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
import com.adeptj.modules.data.jpa.core.EntityManagerFactoryConfig;
import com.adeptj.modules.data.jpa.core.JpaProperties;
import com.adeptj.modules.data.jpa.exception.JpaBootstrapException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import static com.adeptj.modules.data.jpa.JpaConstants.JPA_FACTORY_PID;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NON_JTA_DATASOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATOR_FACTORY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

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

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;

    // <---------------------------------------------- OSGi Internal ------------------------------------------------>

    @Modified
    @Activate
    protected void start(EntityManagerFactoryConfig config) {
        try {
            Map<String, Object> jpaProperties = JpaProperties.from(config);
            jpaProperties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(config.dataSourceName()));
            jpaProperties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
    }
}
