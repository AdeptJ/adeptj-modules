package com.adeptj.modules.commons.utils.service.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ExecutorServices {

    static ExecutorService newExecutorService(ExecutorServiceConfig config) {
        return new ThreadPoolExecutor(
                config.corePoolSize(),
                config.maximumPoolSize(),
                config.keepAliveTime(),
                TimeUnit.valueOf(config.timeUnit()),
                new ArrayBlockingQueue<>(config.workQueueCapacity()),
                new DefaultThreadFactory(config.threadGroupName(), config.executorServiceName()),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
