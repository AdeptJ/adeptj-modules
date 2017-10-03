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

package com.adeptj.modules.commons.logging;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * AdeptJ Logging configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ Logging Configurations",
        description = "Configurations For AdeptJ Logging Mechanism"
)
public @interface LoggingConfig {

    @AttributeDefinition(
            name = "Logging Level",
            description = "Logback Log level",
            options = {
                    @Option(label = "ERROR", value = "ERROR"),
                    @Option(label = "WARN", value = "WARN"),
                    @Option(label = "INFO", value = "INFO"),
                    @Option(label = "DEBUG", value = "DEBUG"),
                    @Option(label = "TRACE", value = "TRACE"),
                    @Option(label = "ALL", value = "ALL"),
                    @Option(label = "OFF", value = "OFF"),
            }
    )
    String level();

    @AttributeDefinition(
            name = "Log File",
            description = "Log file name. Please note that the file is relative to current working directory! " +
                    "If log file name is not provided, logs will only be written to console, if default error.log " +
                    "is provided then logs will be written to both file and console."
    )
    String logFile() default "adeptj-runtime/deployment/logs/";

    @AttributeDefinition(
            name = "Rollover Log File",
            description = "Rollover Log file name (ignored for default error.log file). " +
                    "Please replace the placeholder %s with the file name configured above and be aware " +
                    "that default pattern compresses the file!"
    )
    String rolloverFile() default "adeptj-runtime/deployment/logs/%s-%d{yyyy-MM-dd}.%i.gz";

    @AttributeDefinition(
            name = "Log Pattern",
            description = "Logback Log Pattern (ignored for default error.log file)"
    )
    String pattern() default "%d{yyyy-MM-dd'T'HH:mm:ss.SSS} [%.-23thread] %-5level %logger{100} - %msg%n";

    @AttributeDefinition(
            name = "Log Max History",
            description = "Number of days to keep the log files (ignored for default error.log file)"
    )
    int logMaxHistory() default 30;

    @AttributeDefinition(
            name = "Log Max Size",
            description = "Size of the log logFile in MB (ignored for default error.log file)"
    )
    String logMaxSize() default "10MB";

    @AttributeDefinition(
            name = "Logger Names",
            description = "Logger names to configure Logback Logger"
    )
    String[] loggerNames();

    @AttributeDefinition(
            name = "Logger Additivity",
            description = "If this variable is set to false then the appenders located in the ancestors of this " +
                    "logger will not be used otherwise children inherit the appenders of their ancestors. " +
                    "However, the children of this logger will inherit its appenders, unless the children have their " +
                    "additivity flag set to false too"
    )
    boolean additivity();

    @AttributeDefinition(
            name = "Logger Immediate Flush",
            description = "Whether to Immediate Flush the logs (ignored for default error.log file)"
    )
    boolean immediateFlush() default true;

    @AttributeDefinition(
            name = "Async Appender Required",
            description = "Whether to add the async behaviour to the configured logger " +
                    "(ignored for default error.log file)"
    )
    boolean addAsyncAppender();

    @AttributeDefinition(
            name = "Async Appender Log Queue Size",
            description = "Queue size of the Async appender logs (ignored for default error.log file)"
    )
    int asyncLogQueueSize() default 1000;

    @AttributeDefinition(
            name = "Async Appender Discarding Threshold",
            description = "Size after which Async Appender discards the logs " +
                    "(ignored for default error.log file)"
    )
    int asyncLogDiscardingThreshold() default 0;
}
