package com.adeptj.modules.commons.logging.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import static com.adeptj.modules.commons.logging.internal.LoggerConfigFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Factory for creating Logger configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = LoggerConfig.class, factory = true)
@Component(service = LoggerConfigFactory.class, name = PID, configurationPolicy = REQUIRE)
public class LoggerConfigFactory {

    static final String PID = "com.adeptj.modules.commons.logging.LoggerConfig.factory";
}
