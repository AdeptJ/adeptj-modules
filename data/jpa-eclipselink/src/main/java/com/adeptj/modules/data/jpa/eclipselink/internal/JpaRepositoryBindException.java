package com.adeptj.modules.data.jpa.eclipselink.internal;

import java.io.Serial;

/**
 * Exception thrown when {@link com.adeptj.modules.data.jpa.JpaRepository} could not be bind to.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class JpaRepositoryBindException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2925830631166038486L;

    JpaRepositoryBindException(String message) {
        super(message);
    }
}
