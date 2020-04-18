package com.adeptj.modules.data.jpa;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

public class ConnectionRetrySessionCustomizer implements SessionCustomizer {

    @Override
    public void customize(Session session) throws Exception {
        DatabaseLogin login = (DatabaseLogin)session.getDatasourceLogin();
        login.setConnectionHealthValidatedOnError(false);
        login.setQueryRetryAttemptCount(0);
    }
}
