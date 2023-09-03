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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * This OSGi component registers the {@link LogEntryConsumer} as a {@link LogListener} with {@link LogReaderService}.
 * <p>
 * The registered {@link LogListener} accepts the {@link LogEntry} which contains information such as human-readable
 * message, exception etc.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Component(immediate = true)
public class LogListenerManager {

    private final LogReaderService logReaderService;

    private final LogListener logListener;

    @Activate
    public LogListenerManager(@Reference LogReaderService logReaderService) {
        this.logReaderService = logReaderService;
        this.logListener = new LogEntryConsumer();
        this.logReaderService.addLogListener(this.logListener);
    }

    // <<------------------------------------------- OSGi Internal ------------------------------------------->>

    @Deactivate
    protected void stop() {
        this.logReaderService.removeLogListener(this.logListener);
    }
}
