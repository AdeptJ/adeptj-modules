package com.adeptj.modules.data.jpa;

import org.apache.commons.lang3.StringUtils;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

import static org.osgi.framework.Constants.BUNDLE_ACTIVATOR;
import static org.osgi.namespace.implementation.ImplementationNamespace.IMPLEMENTATION_NAMESPACE;

@Header(name = BUNDLE_ACTIVATOR, value = "${@class}")
public class JpaActivator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private BundleTracker<Bundle> tracker;

    @Override
    public void start(BundleContext context) throws Exception {
        LOGGER.info("JpaActivator: {}", context);
        this.tracker = new BundleTracker<>(context,
                Bundle.ACTIVE | Bundle.STARTING | Bundle.STOPPING | Bundle.RESOLVED | Bundle.INSTALLED,
                new BundleTrackerCustomizer<Bundle>() {
                    @Override
                    public Bundle addingBundle(Bundle bundle, BundleEvent event) {
                        LOGGER.info("Checking bundle: {}", bundle);
                        BundleWiring wiring = bundle.adapt(BundleWiring.class);
                        List<BundleWire> wires = wiring.getRequiredWires(IMPLEMENTATION_NAMESPACE);
                        if (wires != null && !wires.isEmpty()) {
                            for (BundleWire wire : wires) {
                                Map<String, String> directives = wire.getRequirement().getDirectives();
                                if (directives != null && !directives.isEmpty()) {
                                    if (StringUtils.contains(directives.get("filter"), "osgi.implementation=osgi.jpa")) {

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

                    }
                });
        this.tracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        this.tracker.close();
    }
}
