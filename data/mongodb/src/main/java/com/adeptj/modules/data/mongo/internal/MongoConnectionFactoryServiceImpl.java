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

import com.adeptj.modules.data.mongo.api.MongoConnectionFactoryService;
import com.adeptj.modules.data.mongo.api.MongoCrudRepository;
import com.adeptj.modules.data.mongo.exception.MongoRepositoryNotFoundException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementation for {@link MongoConnectionFactoryServiceImpl}
 *
 * @author prince.arora, AdeptJ.
 */
@Component(immediate = true, service = MongoConnectionFactoryService.class)
public class MongoConnectionFactoryServiceImpl implements MongoConnectionFactoryService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MongoConnectionFactoryServiceImpl.class);

    @Reference
    private MongoConnectionProvider connectionProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoCrudRepository getRepository(String unitName) throws MongoRepositoryNotFoundException {
        Optional<MongoCrudRepository> optionalRepository =
                    this.connectionProvider.getRepository(unitName);
        if (!optionalRepository.isPresent()) {
            throw new MongoRepositoryNotFoundException("Repository not found for unit name ["+ unitName +"] ");
        }
        return optionalRepository.get();
    }

}
