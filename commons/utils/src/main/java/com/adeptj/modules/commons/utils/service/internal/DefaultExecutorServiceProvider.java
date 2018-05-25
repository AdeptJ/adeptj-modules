package com.adeptj.modules.commons.utils.service.internal;

import com.adeptj.modules.commons.utils.service.ExecutorServiceProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.concurrent.ExecutorService;

@Component
public class DefaultExecutorServiceProvider implements ExecutorServiceProvider {

    @Reference
    private ExecutorServiceManager executorServiceManager;

    @Override
    public ExecutorService getExecutorService(String name) {
        return this.executorServiceManager.getExecutorService(name);
    }
}
