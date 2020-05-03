package com.adeptj.modules.jaxrs.resteasy.internal;

import com.adeptj.modules.commons.utils.OSGiUtil;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.COMPOSITE_TRACKER_FILTER;

/**
 * CompositeTracker is an OSGi ServiceTracker which registers the services annotated with JAX-RS &#064;Path
 * or JAX-RS &#064;Provider annotation with RESTEasy resource/provider registry.
 * <p>
 * Note: All the registered JAX-RS resources are registered as singleton resource by default.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class CompositeServiceTracker<T> extends ServiceTracker<T, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ProviderManager<T> providerManager;

    private final ResourceManager<T> resourceManager;

    CompositeServiceTracker(BundleContext context, ResteasyProviderFactory providerFactory, Registry registry) {
        super(context, OSGiUtil.anyServiceFilter(context, COMPOSITE_TRACKER_FILTER), null);
        this.providerManager = new ProviderManager<>(providerFactory);
        this.resourceManager = new ResourceManager<>(registry);
    }

    /**
     * Registers the given service with the RESTEasy as a provider or a resource.
     *
     * @param reference The reference to the service being added to this {@link ServiceTracker}.
     * @return The service object to be tracked for the service added to this {@link ServiceTracker}.
     */
    @Override
    public T addingService(ServiceReference<T> reference) {
        T service = super.addingService(reference);
        if (ResteasyUtil.isProvider(reference)) {
            service = this.providerManager.addProvider(reference, service);
        } else if (ResteasyUtil.isResource(reference)) {
            service = this.resourceManager.addResource(reference, service);
        }
        return service;
    }

    /**
     * Removes the given provider or resource from the RESTEasy's managed providers or resources.
     *
     * @param reference The reference to the service being added to this {@link ServiceTracker}.
     * @param service   The service object for the removed service.
     */
    @Override
    public void removedService(ServiceReference<T> reference, T service) {
        try {
            if (ResteasyUtil.isProvider(reference)) {
                this.providerManager.removeProvider(reference, service);
            } else if (ResteasyUtil.isResource(reference)) {
                this.resourceManager.removeResource(reference, service);
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
        super.removedService(reference, service);
    }

    /**
     * Meaningful name of this {@link ServiceTracker}
     *
     * @return a meaningful name of this {@link ServiceTracker}
     */
    @Override
    public String toString() {
        return "CompositeServiceTracker for JAX-RS resources and providers";
    }
}
