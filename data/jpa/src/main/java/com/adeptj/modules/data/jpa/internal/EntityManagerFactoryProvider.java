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
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.apache.commons.lang3.Validate;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static com.adeptj.modules.data.jpa.internal.EntityManagerFactoryProvider.COMPONENT_NAME;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Provides an instance of {@link javax.persistence.EntityManagerFactory} from configured factories.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + EQ + COMPONENT_NAME,
        configurationPolicy = REQUIRE
)
public class EntityManagerFactoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    private static final String DS_NOT_NULL_MSG = "DataSource cannot be null!!";

    private static final String EMF_NULL_MSG = "Could not initialize EntityManagerFactory, most probably persistence.xml not found!!";

    static final String COMPONENT_NAME = "com.adeptj.modules.data.jpa.EntityManagerFactoryProvider.factory";

    private final Lock lock = new ReentrantLock(true);

    private ServiceRegistration<JpaCrudRepository> jpaCrudRepository;

    private EntityManagerFactory emf;

    @Reference
    private DataSourceProvider dataSourceProvider;

    @Reference
    private ValidatorService validatorService;

    // ---------------- INTERNAL ----------------

    @Activate
    protected void start(BundleContext context, EntityManagerFactoryConfig config) {
        this.lock.lock();
        try {
            String unitName = config.unitName();
            Validate.isTrue(isNotEmpty(unitName), "unitName can't be null or empty!!");
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", unitName);
            DataSource ds = Validate.notNull(this.dataSourceProvider.getDataSource(config.dataSourceName()), DS_NOT_NULL_MSG);
            this.emf = new PersistenceProvider()
                    .createEntityManagerFactory(unitName, JpaProperties.create(config, ds, this.validatorService.getValidatorFactory()));
            if (this.emf == null) {
                throw new IllegalStateException(EMF_NULL_MSG);
            } else {
                LOGGER.info("EntityManagerFactory [{}] created for PersistenceUnit: [{}]", this.emf, unitName);
                this.jpaCrudRepository = JpaCrudRepositories.create(unitName, this.emf, context);
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception occurred while creating EntityManagerFactory!!", ex);
            // Close the EntityManagerFactory if it was created earlier but exception occurred later.
            Optional.ofNullable(this.emf).ifPresent(EntityManagerFactory::close);
            // Throw exception so that SCR won't create the component instance.
            throw new JpaBootstrapException(ex);
        } finally {
            this.lock.unlock();
        }
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
        this.lock.lock();
        try {
            LOGGER.info("Closing EntityManagerFactory against PersistenceUnit: [{}]", config.unitName());
            JpaCrudRepositories.dispose(config.unitName(), this.jpaCrudRepository);
            Optional.ofNullable(this.emf).ifPresent(EntityManagerFactory::close);
        } finally {
            this.lock.unlock();
        }
    }
}
