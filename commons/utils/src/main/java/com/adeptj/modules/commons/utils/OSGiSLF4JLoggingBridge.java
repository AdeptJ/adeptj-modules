package com.adeptj.modules.commons.utils;

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
 * OSGiSLF4JLoggingBridge
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, service = OSGiSLF4JLoggingBridge.class)
public class OSGiSLF4JLoggingBridge implements LogListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSGiSLF4JLoggingBridge.class);

    @Reference
    private LogReaderService logReaderService;

    @Override
    public void logged(LogEntry entry) {
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
    }

    // Component Lifecycle Methods

    @Activate
    protected void start() {
        this.logReaderService.addLogListener(this);
    }

    @Deactivate
    protected void stop() {
        this.logReaderService.removeLogListener(this);
    }
}
