package com.adeptj.modules.data.jpa.internal;

/**
 * Exception thrown when {@link com.adeptj.modules.data.jpa.JpaRepository} could not be bind to.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class JpaRepositoryBindException extends RuntimeException {

    private static final long serialVersionUID = -2925830631166038486L;

    JpaRepositoryBindException(String message) {
        super(message);
    }
}
