package com.adeptj.modules.commons.logging.internal;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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
@Component(service = LoggerConfigFactory.class, immediate = true, name = PID, configurationPolicy = REQUIRE)
public class LoggerConfigFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String PID = "com.adeptj.modules.commons.logging.LoggerConfig.factory";

    @Activate
    protected void start(@NotNull LoggerConfig config) {
        LOGGER.info("Adding Logger [{}: {}]", config.logger_name(), config.logger_level());
    }

    @Deactivate
    protected void stop(@NotNull LoggerConfig config) {
        LOGGER.info("Removing Logger [{}: {}]", config.logger_name(), config.logger_level());
    }
}
