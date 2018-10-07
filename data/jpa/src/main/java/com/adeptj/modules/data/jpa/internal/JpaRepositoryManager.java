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
import com.adeptj.modules.data.jpa.api.JpaRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.ValidatorFactory;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.osgi.namespace.implementation.ImplementationNamespace.IMPLEMENTATION_NAMESPACE;

/**
 * Manages the lifecycle of {@link EntityManagerFactory} and {@link JpaRepository}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = JpaRepositoryManager.class)
public class JpaRepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EMF_NULL_MSG = "Could not create EntityManagerFactory, most probably missing persistence.xml!!";

    private BundleContext bundleContext;

    private BundleTracker<Bundle> tracker;

    private List<Bundle> jpaClients;

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;


    Pair<EntityManagerFactory, ServiceRegistration<JpaRepository>> create(EntityManagerFactoryConfig config) {
        EntityManagerFactory emf = null;
        try {
            String unitName = config.unitName();
            Validate.isTrue(StringUtils.isNotEmpty(unitName), "unitName can't be blank!!");
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", unitName);
            DataSource ds = this.dataSourceService.getDataSource(config.dataSourceName());
            ValidatorFactory vf = this.validatorService.getValidatorFactory();
            emf = new PersistenceProvider().createEntityManagerFactory(unitName, JpaProperties.create(config, ds, vf));
            if (emf == null) {
                throw new IllegalStateException(EMF_NULL_MSG);
            }
            LOGGER.info("EntityManagerFactory [{}] created for PersistenceUnit: [{}]", emf, unitName);
            return ImmutablePair.of(emf, JpaRepositories.register(unitName, emf, this.bundleContext));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception occurred while creating EntityManagerFactory!!", ex);
            // Close the EntityManagerFactory if it was created earlier but exception occurred later.
            Optional.ofNullable(emf).ifPresent(EntityManagerFactory::close);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    void dispose(String unitName, Pair<EntityManagerFactory, ServiceRegistration<JpaRepository>> pair) {
        LOGGER.info("Disposing JpaCrudRepository for PersistenceUnit: [{}]", unitName);
        JpaRepositories.unregister(unitName, pair.getRight());
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
        this.tracker = new BundleTracker<>(bundleContext,
                Bundle.ACTIVE | Bundle.STARTING | Bundle.STOPPING | Bundle.RESOLVED | Bundle.INSTALLED,
                new BundleTrackerCustomizer<Bundle>() {
                    @Override
                    public Bundle addingBundle(Bundle bundle, BundleEvent event) {
                        int state = bundle.getState();
                        if (state == Bundle.ACTIVE) {
                            LOGGER.info("Checking bundle: {}", bundle);
                            BundleWiring wiring = bundle.adapt(BundleWiring.class);
                            List<BundleWire> wires = wiring.getRequiredWires(IMPLEMENTATION_NAMESPACE);
                            if (wires != null && !wires.isEmpty()) {
                                for (BundleWire wire : wires) {
                                    Map<String, String> directives = wire.getRequirement().getDirectives();
                                    if (directives != null && !directives.isEmpty()) {
                                        if (StringUtils.contains(directives.get("filter"), "osgi.implementation=osgi.jpa")) {
                                            JpaRepositoryManager.this.jpaClients.add(bundle);
                                            return bundle;
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    public void modifiedBundle(Bundle bundle, BundleEvent event, Bundle object) {

                    }

                    @Override
                    public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
                        if (bundle.getState() == Bundle.UNINSTALLED) {
                            BundleWiring wiring = bundle.adapt(BundleWiring.class);
                            List<BundleWire> wires = wiring.getRequiredWires(IMPLEMENTATION_NAMESPACE);
                            if (wires != null && !wires.isEmpty()) {
                                for (BundleWire wire : wires) {
                                    Map<String, String> directives = wire.getRequirement().getDirectives();
                                    if (directives != null && !directives.isEmpty()) {
                                        if (StringUtils.contains(directives.get("filter"), "osgi.implementation=osgi.jpa")) {
                                            JpaRepositoryManager.this.jpaClients.remove(bundle);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
        this.tracker.open();
        this.jpaClients = new ArrayList<>();
    }

    @Deactivate
    protected void stop(BundleContext bundleContext) {
        this.tracker.close();
    }
}
