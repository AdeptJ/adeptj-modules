package com.adeptj.modules.commons.logging.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * Configuration for Loggers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ Logger Configuration Factory",
        description = "Factory for creating AdeptJ Logger Configurations."
)
public @interface LoggerConfig {

    @AttributeDefinition(
            name = "Logger Name",
            description = "The logger name in java package naming convention form."
    )
    String name();

    @AttributeDefinition(
            name = "Logger Level",
            description = "The logger level as defined in SLF4J log levels.",
            options = {
                    @Option(label = "ERROR", value = "ERROR"),
                    @Option(label = "WARN", value = "WARN"),
                    @Option(label = "INFO", value = "INFO"),
                    @Option(label = "DEBUG", value = "DEBUG"),
                    @Option(label = "TRACE", value = "TRACE")
            })
    String level() default "INFO";

    @AttributeDefinition(
            name = "Logger Additivity",
            description = "Whether to enable the logger additivity."
    )
    boolean additivity();
}
