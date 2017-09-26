package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.framework.Constants.SERVICE_VENDOR;

/**
 * Manager for JpaCrudRepository which will register the repository with OSGi service registry whenever
 * there is a new EntityManagerFactory configuration saved by {@link EntityManagerFactoryProvider}
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Component(immediate = true, service = JpaCrudRepositoryManager.class)
public class JpaCrudRepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaCrudRepositoryManager.class);

    private BundleContext context;

    private Map<String, ServiceRegistration<JpaCrudRepository>> jpaCrudRepositories;

    private Map<String, EntityManagerFactory> entityManagerFactories;

    void registerJpaCrudRepository(String unitName, EntityManagerFactory emf) {
        Dictionary<String, String> properties = new Hashtable<>();
        properties.put(SERVICE_VENDOR, "AdeptJ");
        properties.put(SERVICE_PID, EclipseLinkCrudRepository.class.getName());
        properties.put(SERVICE_DESCRIPTION, "AdeptJ Modules JpaCrudRepository(EclipseLink)");
        properties.put(EntityManagerFactoryBuilder.JPA_UNIT_NAME, unitName);
        LOGGER.info("Registering JpaCrudRepository For PersistenceUnit: [{}]", unitName);
        this.jpaCrudRepositories.put(unitName, this.context.registerService(JpaCrudRepository.class,
                new EclipseLinkCrudRepository(emf), properties));
        this.entityManagerFactories.put(unitName, emf);
    }

    void unregisterJpaCrudRepository(String unitName) {
        try {
            ServiceRegistration<JpaCrudRepository> svcReg = this.jpaCrudRepositories.remove(unitName);
            if (svcReg == null) {
                LOGGER.warn("No JpaCrudRepository found for PersistenceUnit: [{}]", unitName);
            } else {
                LOGGER.info("un-registering JpaCrudRepository For PersistenceUnit: [{}]", unitName);
                svcReg.unregister();
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while un-registering JpaCrudRepository service!!", ex);
        }
    }

    // Lifecycle Methods

    @Activate
    protected void start(BundleContext context) {
        this.context = context;
        this.jpaCrudRepositories = new ConcurrentHashMap<>();
        this.entityManagerFactories = new ConcurrentHashMap<>();
    }

    @Deactivate
    protected void stop() {
        this.unregisterJpaCrudRepositories();
        this.closeEntityManagerFactories();
    }

    private void unregisterJpaCrudRepositories() {
        this.jpaCrudRepositories.forEach((unitName, svcReg) -> {
            try {
                svcReg.unregister();
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while un-registering JpaCrudRepository services!!", ex);
            }
        });
    }

    private void closeEntityManagerFactories() {
        this.entityManagerFactories
                .entrySet()
                .stream()
                .filter(emfEntry -> emfEntry.getValue().isOpen())
                .forEach(this::closeEntityManagerFactory);
    }

    private void closeEntityManagerFactory(Map.Entry<String, EntityManagerFactory> entry) {
        try {
            entry.getValue().close();
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while closing EntityManagerFactory!!", ex);
        }
    }
}
