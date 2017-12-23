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

package com.adeptj.modules.data.mongo.api;

import com.adeptj.modules.data.mongo.exception.MongoRepositoryNotFoundException;

import java.util.Optional;

/**
 * Service to provide mongodb repository.
 *
 * @author prince.arora, AdeptJ.
 */
public interface MongoConnectionFactoryService {

    /**
     * Returns {@link Optional} of {@link MongoCrudRepository} for a given unit name.
     * <p>
     * Unit name should be same as given in mongoDB connection factory configuration.
     * Throws {@link MongoRepositoryNotFoundException} if unitName does not match any configuration.
     *
     * @param unitName  string identifier for mongodb configuration.
     * @return  {@link MongoCrudRepository} mongo repository for given unit.
     *
     * @throws MongoRepositoryNotFoundException
     */
    MongoCrudRepository getRepository(String unitName) throws MongoRepositoryNotFoundException;
}
