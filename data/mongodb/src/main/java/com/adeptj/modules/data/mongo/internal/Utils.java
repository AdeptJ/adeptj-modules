/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

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
