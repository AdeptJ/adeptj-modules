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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.osgi.service.log.LogService.LOG_WARNING;

/**
 * OSGi {@link LogListener} which acts as a bridge for {@link LogReaderService} to send
 * ERROR and WARN logs to SLF4J {@link Logger}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, service = OSGiSLF4JLoggingBridge.class)
public class OSGiSLF4JLoggingBridge {

    private static final String DEFAULT_LOGGER = "com.adeptj.modules.commons.logging";

    private static final Logger LOGGER = LoggerFactory.getLogger(DEFAULT_LOGGER);

    @Reference
    private LogReaderService logReaderService;

    private final LogListener logListener = (LogEntry entry) -> {
        switch (entry.getLevel()) {
            case LOG_ERROR:
                LOGGER.error(entry.getMessage(), entry.getException());
                break;
            case LOG_WARNING:
                LOGGER.warn(entry.getMessage(), entry.getException());
                break;
            default:
                // do nothing, we are not interested in other log levels.
        }
    };

    // Component Lifecycle Methods

    @Activate
    protected void start() {
        this.logReaderService.addLogListener(this.logListener);
    }

    @Deactivate
    protected void stop() {
        this.logReaderService.removeLogListener(this.logListener);
    }
}
