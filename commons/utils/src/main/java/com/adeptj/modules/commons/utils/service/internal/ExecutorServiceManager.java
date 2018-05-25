package com.adeptj.modules.commons.utils.service.internal;

import com.adeptj.modules.commons.utils.Loggers;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * Manages {@link ExecutorService} lifecycle from creation to shutdown of {@link ExecutorService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = ExecutorServiceManager.class)
public class ExecutorServiceManager {

    private static final Logger LOGGER = Loggers.get(MethodHandles.lookup().lookupClass());

    private ConcurrentMap<String, ExecutorService> executorServices = new ConcurrentHashMap<>();

    void createExecutorService(ExecutorServiceConfig config) {
        String name = config.executorServiceName();
        Validate.isTrue(StringUtils.isNotEmpty(name), "ExecutorService name can't be blank!!");
        this.executorServices.put(name, ExecutorServices.newExecutorService(config));
        LOGGER.info("ExecutorService: [{}] created!!", name);
    }

    ExecutorService getExecutorService(String name) {
        ExecutorService executorService = this.executorServices.get(name);
        if (executorService == null) {
            throw new IllegalStateException(String.format("No ExecutorService with name [%s] configured!!", name));
        }
        return executorService;
    }

    void shutdownExecutorService(String name) {
        Optional.ofNullable(this.executorServices.remove(name))
                .ifPresent(executorService -> {
                    try {
                        executorService.shutdown();
                        LOGGER.info("ExecutorService: [{}] shutdown!!", name);
                    } catch (Exception ex) { // NOSONAR
                        LOGGER.error("Exception while ExecutorService shutdown!!", ex);
                    }
                });
    }
}
