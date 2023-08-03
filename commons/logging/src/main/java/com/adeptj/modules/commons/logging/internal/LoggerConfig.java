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
        description = "Factory for creating AdeptJ Logger Configurations.",
        localization = "OSGI-INF/l10n/metatype"
)
public @interface LoggerConfig {

    @AttributeDefinition(
            name = "Logger Names",
            description = "%logger.names.desc"
    )
    String[] logger_names();

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

    // name hint non-editable property
    String webconsole_configurationFactory_nameHint() default
            "Logger ({" + "logger.names" + "}" + ": " + "{" + "logger.level" + "})"; // NOSONAR
}
