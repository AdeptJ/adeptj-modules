package com.adeptj.modules.security.core.identitystore;


import com.adeptj.modules.security.core.Authenticator;
import com.adeptj.modules.security.core.credential.Credential;

public interface IdentityStore {

    int DEFAULT_PRIORITY = 0;

    /**
     * Determines the order of invocation for multiple {@link IdentityStore}s.
     * Stores with a higher priority value are consulted first.
     *
     * @return The priority value. Higher values indicate higher priorities.
     */
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Provides a meaningful name which can be used by {@link Authenticator}.
     *
     * @return a meaningful name.
     */
    default String getName() {
        return this.getClass().getName();
    }

    boolean canValidate(Credential credential);

    CredentialValidationOutcome validate(Credential credential);
}
