package com.adeptj.modules.commons.utils.service.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(name = "AdeptJ ExecutorService Config")
public @interface ExecutorServiceConfig {

    @AttributeDefinition(
            name = "Core Pool Size",
            description = "The number of threads to keep in the pool, even if they are idle."
    )
    String executorServiceName() default "AdeptJ ExecutorService";

    @AttributeDefinition(
            name = "Core Pool Size",
            description = "The number of threads to keep in the pool, even if they are idle."
    )
    String threadGroupName() default "AdeptJ Thread Pool";

    @AttributeDefinition(
            name = "Core Pool Size",
            description = "The number of threads to keep in the pool, even if they are idle."
    )
    int corePoolSize() default 8;

    @AttributeDefinition(
            name = "Core Pool Size",
            description = "The number of threads to keep in the pool, even if they are idle."
    )
    int maximumPoolSize() default 16 * 8;

    @AttributeDefinition(
            name = "Core Pool Size",
            description = "The number of threads to keep in the pool, even if they are idle."
    )
    int workQueueCapacity() default 100;

    @AttributeDefinition(
            name = "Core Pool Size",
            description = "The number of threads to keep in the pool, even if they are idle."
    )
    long keepAliveTime();

    @AttributeDefinition(
            name = "Keep Alive Time's TimeUnit",
            description = "Keep Alive Time's TimeUnit in java.util.concurrent.TimeUnit.",
            options = {
                    @Option(label = "MILLISECONDS", value = "MILLISECONDS"),
                    @Option(label = "SECONDS", value = "SECONDS"),
            }
    )
    String timeUnit();
}
