package com.adeptj.modules.jaxrs.resteasy;

/**
 * Exception to be thrown on RESTEasy initialization.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class JaxRSInitializationException extends RuntimeException {

    public JaxRSInitializationException(String message) {
        super(message);
    }

    public JaxRSInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaxRSInitializationException(Throwable cause) {
        super(cause);
    }
}
