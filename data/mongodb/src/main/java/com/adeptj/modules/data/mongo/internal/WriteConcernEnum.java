package com.adeptj.modules.data.mongo.internal;

/**
 * Write concern for mongo connection.
 *
 * @author prince.arora, AdeptJ.
 */
public enum WriteConcernEnum {

    ACKNOWLEDGED,
    JOURNALED,
    MAJORITY,
    UNACKNOWLEDGED,

}
