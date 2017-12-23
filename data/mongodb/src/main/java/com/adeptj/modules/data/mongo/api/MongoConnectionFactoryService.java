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
