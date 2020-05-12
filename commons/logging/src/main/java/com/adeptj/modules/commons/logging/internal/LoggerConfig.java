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
            description = "The logger name in Java package naming convention form."
    )
    String logger_name();

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
    String logger_level() default "INFO";

    @AttributeDefinition(
            name = "Logger Additivity",
            description = "Whether to enable the logger additivity."
    )
    boolean logger_additivity();

    // name hint non editable property
    String webconsole_configurationFactory_nameHint() default
            "Logger [{" + "logger.name" + "}" + ": " + "{" + "logger.level" + "}]"; // NOSONAR
}
