package com.adeptj.modules.commons.utils.service.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class DefaultThreadFactory implements ThreadFactory {

    private static final int DEFAULT_STACK_SIZE = 0;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final ThreadGroup threadGroup;

    private final String namePrefix;

    DefaultThreadFactory(String threadGroupName, String executorServiceName) {
        this.threadGroup = new ThreadGroup(threadGroupName);
        this.namePrefix = executorServiceName + "-Pool" + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(this.threadGroup, r, this.namePrefix + this.threadNumber.getAndIncrement(),
                DEFAULT_STACK_SIZE);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
