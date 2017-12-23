package com.adeptj.modules.data.mongo.exception;

/**
 * Exception if mongodb repository not found.
 *
 * @author prince.arora, Adeptj.
 */
public class MongoRepositoryNotFoundException extends RuntimeException {

    public MongoRepositoryNotFoundException(String message) {
        super(message);
    }

    public MongoRepositoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
