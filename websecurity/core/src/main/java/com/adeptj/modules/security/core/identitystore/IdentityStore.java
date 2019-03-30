package com.adeptj.modules.security.core.identitystore;


import com.adeptj.modules.security.core.Authenticator;
import com.adeptj.modules.security.core.credential.Credential;

public interface IdentityStore {

    /**
     * Determines the order of invocation for multiple {@link IdentityStore}s.
     * Stores with a higher priority value are consulted first.
     *
     * @return The priority value. Higher values indicate higher priorities.
     */
    default int priority() {
        return 100;
    }

    /**
     * Provides a meaningful name which can be used by {@link Authenticator}.
     *
     * @return a meaningful name.
     */
    default String getName() {
        return this.getClass().getName();
    }

    boolean validate(Credential credential);
}
