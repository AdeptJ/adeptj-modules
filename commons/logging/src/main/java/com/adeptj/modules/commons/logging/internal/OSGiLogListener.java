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

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.osgi.service.log.LogService.LOG_WARNING;

/**
 * Simple implementation of OSGi {@link LogListener} which logs the given {@link LogEntry}
 * to underlying logging framework with the help of SLF4J.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OSGiLogListener implements LogListener {

    private static final String OSGI_LOGGER = "com.adeptj.modules.commons.osgi.logger";

    private static final Logger LOGGER = LoggerFactory.getLogger(OSGI_LOGGER);

    @Override
    public void logged(LogEntry entry) {
        switch (entry.getLevel()) {
            case LOG_ERROR:
                LOGGER.error(entry.getMessage(), entry.getException());
                break;
            case LOG_WARNING:
                LOGGER.warn(entry.getMessage());
                break;
            default:
                // do nothing, we are not interested in other log levels.
        }
    }
}
