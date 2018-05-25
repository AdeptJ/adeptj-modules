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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.ValidatorFactory;
import java.util.Optional;

/**
 * Manages the lifecycle of {@link EntityManagerFactory} and {@link JpaCrudRepository}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = JpaCrudRepositoryManager.class)
public class JpaCrudRepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaCrudRepositoryManager.class);

    private static final String EMF_NULL_MSG = "Could not register EntityManagerFactory, most probably missing persistence.xml!!";

    private BundleContext bundleContext;

    @Reference
    private DataSourceProvider dataSourceProvider;

    @Reference
    private ValidatorService validatorService;


    Pair<EntityManagerFactory, ServiceRegistration<JpaCrudRepository>> create(EntityManagerFactoryConfig config) {
        EntityManagerFactory emf = null;
        try {
            String unitName = config.unitName();
            Validate.isTrue(StringUtils.isNotEmpty(unitName), "unitName can't be blank!!");
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", unitName);
            DataSource ds = this.dataSourceProvider.getDataSource(config.dataSourceName());
            ValidatorFactory vf = this.validatorService.getValidatorFactory();
            emf = new PersistenceProvider().createEntityManagerFactory(unitName, JpaProperties.create(config, ds, vf));
            if (emf == null) {
                throw new IllegalStateException(EMF_NULL_MSG);
            }
            LOGGER.info("EntityManagerFactory [{}] created for PersistenceUnit: [{}]", emf, unitName);
            return ImmutablePair.of(emf, JpaCrudRepositories.register(unitName, emf, this.bundleContext));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception occurred while creating EntityManagerFactory!!", ex);
            // Close the EntityManagerFactory if it was created earlier but exception occurred later.
            Optional.ofNullable(emf).ifPresent(EntityManagerFactory::close);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    void dispose(String unitName, Pair<EntityManagerFactory, ServiceRegistration<JpaCrudRepository>> pair) {
        LOGGER.info("Disposing JpaCrudRepository for PersistenceUnit: [{}]", unitName);
        JpaCrudRepositories.unregister(unitName, pair.getRight());
        LOGGER.info("Closing EntityManagerFactory against PersistenceUnit: [{}]", unitName);
        try {
            Optional.ofNullable(pair.getLeft()).ifPresent(EntityManagerFactory::close);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception occurred while closing EntityManagerFactory!!", ex);
        }
    }

    // ------------------------------------------------ INTERNAL ------------------------------------------------

    @Activate
    protected void start(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
