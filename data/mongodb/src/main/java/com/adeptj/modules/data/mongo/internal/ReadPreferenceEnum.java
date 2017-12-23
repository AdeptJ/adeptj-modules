package com.adeptj.modules.data.mongo.internal;

/**
 * Read Preference for mongo connection.
 *
 * @author prince.arora, AdeptJ.
 */
public enum ReadPreferenceEnum {

    PRIMARY,
    SECONDARY,
    SECONDARY_PREFERRED,
    PRIMARY_PREFERRED,
    NEAREST;
}
