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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.adeptj.modules.commons.logging.LoggingConfig;
import com.adeptj.modules.commons.utils.Constants;
import com.adeptj.runtime.tools.logging.LogbackConfig;
import com.adeptj.runtime.tools.logging.LogbackManager;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.adeptj.modules.commons.logging.internal.LoggingConfigFactory.COMPONENT_NAME;
import static com.adeptj.runtime.tools.logging.LogbackManager.APPENDER_CONSOLE;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Configuration Factory for AdeptJ Logging mechanism.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = LoggingConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + Constants.EQ + COMPONENT_NAME,
        configurationPolicy = REQUIRE
)
public class LoggingConfigFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConfigFactory.class);

    private static final String DEFAULT_LOG_FILE = "error.log";

    private static final String SLASH = "/";

    static final String COMPONENT_NAME = "com.adeptj.modules.commons.logging.LoggingConfigFactory.factory";

    @Activate
    protected void start(LoggingConfig config) {
        String level = config.level();
        String logFile = config.logFile();
        String[] loggerNames = config.loggerNames();
        boolean additivity = config.additivity();
        LogbackManager logbackMgr = LogbackManager.INSTANCE;
        // If configuration is for error.log, add the configured loggers to both FILE and CONSOLE appender.
        if (StringUtils.endsWith(logFile, DEFAULT_LOG_FILE)) {
            logbackMgr.addLogger(LogbackConfig.builder()
                    .loggers(loggerNames)
                    .level(level)
                    .additivity(additivity)
                    .appenders(logbackMgr.getAppenders())
                    .build());
            return;
        }
        // If no log file provided then add the configured loggers only to CONSOLE appender.
        if (StringUtils.endsWith(logFile, SLASH)) {
            logbackMgr.addLogger(LogbackConfig.builder()
                    .loggers(loggerNames)
                    .level(level)
                    .additivity(additivity)
                    .appender(logbackMgr.getAppender(APPENDER_CONSOLE))
                    .build());
            return;
        }
        String rolloverFile = config.rolloverFile();
        String pattern = config.pattern();
        int logMaxHistory = config.logMaxHistory();
        String logMaxSize = config.logMaxSize();
        boolean immediateFlush = config.immediateFlush();
        boolean addAsyncAppender = config.addAsyncAppender();
        int asyncLogQueueSize = config.asyncLogQueueSize();
        int asyncLogDiscardingThreshold = config.asyncLogDiscardingThreshold();
        LogbackConfig logbackConfig = LogbackConfig.builder()
                .appenderName(UUID.randomUUID().toString())
                .asyncAppenderName(UUID.randomUUID().toString())
                .level(level)
                .logFile(logFile)
                .additivity(additivity)
                .rolloverFile(rolloverFile)
                .pattern(pattern)
                .logMaxHistory(logMaxHistory)
                .logMaxSize(logMaxSize)
                .immediateFlush(immediateFlush)
                .addAsyncAppender(addAsyncAppender)
                .asyncLogQueueSize(asyncLogQueueSize)
                .asyncLogDiscardingThreshold(asyncLogDiscardingThreshold)
                .loggers(loggerNames)
                .build();
        RollingFileAppender<ILoggingEvent> fileAppender = logbackMgr.createRollingFileAppender(logbackConfig);
        logbackConfig.getAppenders().add(fileAppender);
        if (logbackConfig.isAddAsyncAppender()) {
            logbackConfig.setAsyncAppender(fileAppender);
            logbackMgr.addAppender(fileAppender).createAsyncAppender(logbackConfig);
        }
        logbackMgr.addLogger(logbackConfig);
    }

    @Deactivate
    protected void stop(LoggingConfig config) {
    }
}
