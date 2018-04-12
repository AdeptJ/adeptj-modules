package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Executors;

import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.framework.Constants.SERVICE_VENDOR;
import static org.osgi.service.jpa.EntityManagerFactoryBuilder.JPA_UNIT_NAME;

/**
 * This class is a utility which registers the {@link JpaCrudRepository} with OSGi service registry whenever
 * there is a new EntityManagerFactory configuration saved by {@link EntityManagerFactoryProvider}
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
final class JpaCrudRepositories {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaCrudRepositories.class);

    static ServiceRegistration<JpaCrudRepository> create(String unitName, EntityManagerFactory emf, BundleContext context) {
        Dictionary<String, String> properties = new Hashtable<>();
        properties.put(SERVICE_VENDOR, "AdeptJ");
        properties.put(SERVICE_PID, EclipseLinkCrudRepository.class.getName());
        properties.put(SERVICE_DESCRIPTION, "AdeptJ JpaCrudRepository(EclipseLink)");
        properties.put(JPA_UNIT_NAME, unitName);
        LOGGER.info("Registering JpaCrudRepository For PersistenceUnit: [{}]", unitName);
        return context.registerService(JpaCrudRepository.class,
                new EclipseLinkCrudRepository(emf, Executors.newSingleThreadScheduledExecutor()), properties);
    }

    static void dispose(String unitName, ServiceRegistration<JpaCrudRepository> svcReg) {
        try {
            LOGGER.info("Disposing JpaCrudRepository For PersistenceUnit: [{}]", unitName);
            svcReg.unregister();
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while disposing JpaCrudRepository service!!", ex);
        }
    }
}
