package com.adeptj.modules.data.jpa.exception;

/**
 * Exception thrown when {@link com.adeptj.modules.data.jpa.JpaRepository} could not be bind to.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JpaRepositoryBindException extends RuntimeException {

    private static final long serialVersionUID = -2925830631166038486L;

    public JpaRepositoryBindException(Throwable throwable) {
        super(throwable);
    }
}
