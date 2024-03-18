/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.commons.logging.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static com.adeptj.modules.commons.logging.internal.LoggerConfigFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Factory for creating Logger configurations.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Designate(ocd = LoggerConfigFactory.LoggerConfig.class, factory = true)
@Component(service = LoggerConfigFactory.class, name = PID, configurationPolicy = REQUIRE)
public class LoggerConfigFactory {

    static final String PID = "com.adeptj.modules.commons.logging.LoggerConfig.factory";

    /**
     * Configuration for Loggers.
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    @ObjectClassDefinition(
            name = "AdeptJ Logger Configuration Factory",
            description = "Factory for creating AdeptJ Logger Configurations.",
            localization = "OSGI-INF/l10n/metatype"
    )
    public @interface LoggerConfig {

        @AttributeDefinition(
                name = "Logger Names",
                description = "%logger.names.desc"
        )
        String[] logger_names(); // NOSONAR

        @AttributeDefinition(
                name = "Logger Level",
                description = "The logger level as defined in SLF4J log levels.",
                options = {
                        @Option(label = "Error", value = "ERROR"),
                        @Option(label = "Warn", value = "WARN"),
                        @Option(label = "Info", value = "INFO"),
                        @Option(label = "Debug", value = "DEBUG"),
                        @Option(label = "Trace", value = "TRACE")
                })
        String logger_level() default "INFO"; // NOSONAR

        // name hint non-editable property
        String webconsole_configurationFactory_nameHint() default
                "Logger ({" + "logger.names" + "}" + ": " + "{" + "logger.level" + "})"; // NOSONAR
    }
}
