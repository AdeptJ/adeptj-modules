package com.adeptj.modules.data.mongo.internal;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * Internal utility for mongodb connector.
 *
 * @author prince.arora, AdeptJ.
 */
public final class Utils {

    public static final String PROVIDER_COMPONENT_NAME = "com.adeptj.modules.data.mongo.MongoConnectionProvider.factory";
    public static final String PROVIDER_FACTORY_NAME = "AdeptJ MongoDB Connection Provider";

    /**
     * Prepares {@link ReadPreference} from given {@link ReadPreferenceEnum}
     *
     * @param readPreferenceEnum    Read preference enum value provided in config
     * @return  Read preference for mongo client options
     */
    public static ReadPreference readPreference(ReadPreferenceEnum readPreferenceEnum) {
        switch (readPreferenceEnum) {
            case NEAREST:
                return ReadPreference.nearest();

            case PRIMARY:
                return ReadPreference.primary();

            case SECONDARY:
                return ReadPreference.secondary();

            case PRIMARY_PREFERRED:
                return ReadPreference.primaryPreferred();

            case SECONDARY_PREFERRED:
                return ReadPreference.secondaryPreferred();

            default:
                return null;
        }
    }

    /**
     * Prepares {@link WriteConcern} from given {@link WriteConcernEnum}
     *
     * @param writeConcernEnum  write concern provided in config.
     * @return  write concern for mongo client option.
     */
    public static WriteConcern writeConcern(WriteConcernEnum writeConcernEnum) {
        switch (writeConcernEnum) {
            case MAJORITY:
                return WriteConcern.MAJORITY;

            case JOURNALED:
                return WriteConcern.JOURNALED;

            case ACKNOWLEDGED:
                return WriteConcern.ACKNOWLEDGED;

            case UNACKNOWLEDGED:
                return WriteConcern.UNACKNOWLEDGED;

            default:
                return null;
        }
    }
}
