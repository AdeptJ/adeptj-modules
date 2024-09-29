/*
 *******************************************************************************
 * Copyright (c) 2009, 2016  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Jaro Kuruc  - Initial API and implementation.
 *      Tomas Kraus - EclipseLink 2.7 integration.
 ******************************************************************************
 */
package com.adeptj.modules.data.jpa.eclipselink.extension;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.LogCategory;
import org.eclipse.persistence.logging.LogLevel;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * EclipseLink logger bridge over SLF4J.
 * <p>
 * Borrowed (with much appreciation) from EclipseLink's org.eclipse.persistence.logging.slf4j.SLF4JLogger.
 * <p>
 * EclipseLink's SLF4JLogger class is not exported by the containing bundle and that results in a
 * {@link ClassNotFoundException} when EclipseLink tries to initialize the EntityManagerFactory with the
 * eclipselink.logging.logger property having the fully qualified class name of this class as value.
 *
 * @author Jaro Kuruc  - Initial API and implementation.
 * @author Tomas Kraus - EclipseLink 2.7 integration.
 */
public class SLF4JLogger extends AbstractSessionLog {

    /**
     * Logging levels for individual logging categories.
     */
    private final LogLevel[] logLevels;

    /**
     * Creates an instance of EclipseLink logger bridge over SLF4J
     */
    public SLF4JLogger() {
        // Set default logging levels for all logging categories.
        final byte defaultLevel = LogLevel.toValue(level).getId();
        this.logLevels = new LogLevel[LogCategory.length];
        Arrays.fill(this.logLevels, LogLevel.toValue(defaultLevel));
    }

    /**
     * Get the logging level for the default logging category.
     *
     * @return level Current logging level for default the default logging category.
     */
    @Override
    public int getLevel() {
        return this.logLevels[LogCategory.ALL.getId()].getId();
    }

    /**
     * Get the logging level for the specified logging category.
     *
     * @param categoryName The {@link String} representation of an EclipseLink logging category.
     * @return level Current logging level for default the default logging category.
     */
    @Override
    public int getLevel(final String categoryName) {
        final LogCategory category = LogCategory.toValue(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Unknown logging category name.");
        }
        return this.logLevels[category.getId()].getId();
    }

    /**
     * Set the logging level for the default logging category.
     *
     * @param level The logging level to be set.
     */
    @Override
    public void setLevel(final int level) {
        super.setLevel(level);
        this.logLevels[LogCategory.ALL.getId()] = LogLevel.toValue(level);
    }

    /**
     * Set the logging level for the specified logging category.
     *
     * @param level        The logging level to be set.
     * @param categoryName The {@link String} representation of an EclipseLink logging category.
     */
    @Override
    public void setLevel(final int level, final String categoryName) {
        final LogCategory category = LogCategory.toValue(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Unknown logging category name.");
        }
        this.logLevels[category.getId()] = LogLevel.toValue(level);
    }

    /**
     * Check if a message of the given level would actually be logged under logging level for the default logging
     * category.
     *
     * @param level Message logging level.
     * @return Value of {@code true} if the given message logging level will be logged or {@code false} otherwise.
     */
    @Override
    public boolean shouldLog(final int level) {
        return this.logLevels[LogCategory.ALL.getId()].shouldLog((byte) level);
    }

    /**
     * Check if a message of the given level would actually be logged under logging level for the specified logging
     * category.
     *
     * @param level        Message logging level.
     * @param categoryName The {@link String} representation of an EclipseLink logging category.
     * @return Value of {@code true} if the given message logging level will be logged or {@code false} otherwise.
     */
    @Override
    public boolean shouldLog(final int level, final String categoryName) {
        final LogCategory category = LogCategory.toValue(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Unknown logging category name.");
        }
        return this.logLevels[category.getId()].shouldLog((byte) level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(final SessionLogEntry logEntry) {
        if (logEntry == null) {
            return;
        }
        final LogCategory category = LogCategory.toValue(logEntry.getNameSpace());
        if (category == null) {
            // Let's just silently return.
            return;
        }
        final byte levelId = (byte) logEntry.getLevel();
        if (this.logLevels[category.getId()].shouldLog(levelId)) {
            final Logger logger = LoggerFactory.getLogger(category.getNameSpace());
            final LogLevel level = LogLevel.toValue(levelId);
            // If EclipseLink LogLevel is ALL or FINEST but SLF4J TRACE is not enabled, return right away.
            if ((level == LogLevel.ALL || level == LogLevel.FINEST) && !logger.isTraceEnabled()) {
                return;
            }
            // If EclipseLink LogLevel is FINER or FINE but SLF4J DEBUG is not enabled, return right away.
            if ((level == LogLevel.FINER || level == LogLevel.FINE) && !logger.isDebugEnabled()) {
                return;
            }
            this.doLog(logger, logEntry, level);
        }
    }

    private void doLog(final Logger logger, final SessionLogEntry logEntry, final LogLevel level) {
        if (logEntry.hasException()) {
            if (super.shouldLogExceptionStackTrace()) {
                // Message is rendered on EclipseLink side. SLF4J gets final String. Exception is passed too.
                this.doLogSLF4J(logger, level, super.formatMessage(logEntry), logEntry.getException());
            } else {
                // Exception message is rendered on EclipseLink side. SLF4J gets final String.
                this.doLogSLF4J(logger, level, logEntry.getException().toString(), null);
            }
        } else {
            // Message is rendered on EclipseLink side. SLF4J gets final String.
            this.doLogSLF4J(logger, level, super.formatMessage(logEntry), null);
        }
    }

    private void doLogSLF4J(final Logger logger, final LogLevel level, final String msg, final Throwable t) {
        switch (level) {
            case ALL, FINEST -> logger.trace(msg, t);
            case FINER, FINE -> logger.debug(msg, t);
            case CONFIG, INFO -> logger.info(msg, t);
            case WARNING -> logger.warn(msg, t);
            case SEVERE -> logger.error(msg, t);
        }
    }
}
