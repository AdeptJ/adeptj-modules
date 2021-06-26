package com.adeptj.modules.data.jpa;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class PostgresCustomizer implements SessionCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void customize(Session session) throws Exception {
        DatabaseLogin login = (DatabaseLogin)session.getDatasourceLogin();
        DatabasePlatform platform = login.getPlatform();
        LOGGER.info("{} setting supportsAutoCommit to false", platform.getClass());
        platform.setSupportsAutoCommit(false);
    }
}
