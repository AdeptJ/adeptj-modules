package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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

    private Map<String, ServiceRegistration<JpaCrudRepository>> jpaCrudRepositoryRegistrations;

    void registerJpaCrudRepository(String unitName, EntityManagerFactory entityManagerFactory) {
        Dictionary<String , String> properties = new Hashtable<>();
        properties.put(Constants.SERVICE_VENDOR, "AdeptJ");
        properties.put(Constants.SERVICE_PID, EclipseLinkCrudRepository.class.getName());
        properties.put(Constants.SERVICE_DESCRIPTION, "AdeptJ Modules JpaCrudRepository");
        properties.put(EntityManagerFactoryBuilder.JPA_UNIT_NAME, unitName);
        LOGGER.info("Registering JpaCrudRepository For PersistenceUnit: [{}]", unitName);
        this.jpaCrudRepositoryRegistrations.put(unitName, this.context.registerService(JpaCrudRepository.class,
                new EclipseLinkCrudRepository(entityManagerFactory), properties));
    }

    void unregisterJpaCrudRepository(String unitName) {
        try {
            ServiceRegistration<JpaCrudRepository> svcReg = this.jpaCrudRepositoryRegistrations.remove(unitName);
            if (svcReg == null) {
                LOGGER.info("No JpaCrudRepository found for PersistenceUnit: [{}]", unitName);
            } else {
                LOGGER.info("Un-registering JpaCrudRepository For PersistenceUnit: [{}]", unitName);
                svcReg.unregister();
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while unregistering JpaCrudRepository service!!", ex);
        }
    }

    // LifeCycle Methods

    @Activate
    protected void activate(BundleContext context) {
        this.context = context;
        this.jpaCrudRepositoryRegistrations = new HashMap<>();
    }

    @Deactivate
    protected void deactivate() {
        try {
            this.jpaCrudRepositoryRegistrations.forEach((unitName, serviceRegistration) -> serviceRegistration.unregister());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while unregistering JpaCrudRepository services!!", ex);
        }
    }
}
