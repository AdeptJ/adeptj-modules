package com.adeptj.modules.commons.logging.internal;

import com.adeptj.runtime.extensions.logging.LogbackManager;
import com.adeptj.runtime.extensions.logging.core.LogbackConfig;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.commons.logging.internal.LoggerConfigFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Factory for creating Logger configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = LoggerConfig.class, factory = true)
@Component(immediate = true, name = PID, configurationPolicy = REQUIRE)
public class LoggerConfigFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String PID = "com.adeptj.modules.commons.logging.LoggerConfig.factory";

    private final LogbackManager logbackManager;

    @Activate
    public LoggerConfigFactory(@Reference LogbackManager logbackManager, @NotNull LoggerConfig config) {
        this.logbackManager = logbackManager;
        this.logbackManager.addLogger(LogbackConfig.builder()
                .logger(config.logger_name())
                .level(config.logger_level())
                .additivity(config.logger_additivity())
                .build());
        LOGGER.info("Added logger: {} with level: {}", config.logger_name(), config.logger_level());
    }

    @Deactivate
    protected void stop(@NotNull LoggerConfig config) {
        this.logbackManager.detachAppenders(config.logger_name());
        LOGGER.info("Removed logger: {} with level: {}", config.logger_name(), config.logger_level());
    }
}
