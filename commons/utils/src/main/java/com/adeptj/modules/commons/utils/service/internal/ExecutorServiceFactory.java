package com.adeptj.modules.commons.utils.service.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * A factory for creating {@link java.util.concurrent.ExecutorService} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = ExecutorServiceConfig.class, factory = true)
@Component(
        immediate = true,
        name = "ExecutorServiceFactory.factory",
        property = SERVICE_PID + EQ + "ExecutorServiceFactory.factory",
        configurationPolicy = REQUIRE
)
public class ExecutorServiceFactory {

    @Reference
    private ExecutorServiceManager executorServiceManager;

    @Activate
    protected void start(ExecutorServiceConfig config) {
        this.executorServiceManager.createExecutorService(config);
    }

    @Deactivate
    protected void stop(ExecutorServiceConfig config) {
        this.executorServiceManager.shutdownExecutorService(config.executorServiceName());
    }
}
