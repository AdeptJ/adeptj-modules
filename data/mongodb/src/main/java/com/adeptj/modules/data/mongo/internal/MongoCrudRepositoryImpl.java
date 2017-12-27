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

import com.adeptj.modules.data.mongo.api.BaseEntity;
import com.adeptj.modules.data.mongo.api.MongoCrudRepository;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mongodb.morphia.mapping.Mapper.ID_KEY;

/**
 * Implementation for MongoCrud operations {@link MongoCrudRepository}
 *
 * @author prince.arora, AdeptJ.
 */
public class MongoCrudRepositoryImpl implements MongoCrudRepository {

    final private Datastore datastore;

    public MongoCrudRepositoryImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Datastore getDatastore() {
        return this.datastore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> Object save(T t) {
        return this.datastore.save(t).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> tClass, FindOptions findOptions) {
        return (Objects.nonNull(findOptions)) ?
                this.datastore.find(tClass).asList(findOptions) :
                this.datastore.find(tClass).asList();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> tClass, FindOptions findOptions, Map<String, Object> criteriaMap) {
        Query<T> tQuery = this.datastore.createQuery(tClass);
        if (criteriaMap.size() > 0) {
            criteriaMap.forEach(
                (key, value) -> {
                    tQuery.field(key)
                            .equal(value);
                }
            );
        }
        return (Objects.nonNull(findOptions)) ?
                tQuery.asList(findOptions) : tQuery.asList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T findOne(Class<T> tClass, FindOptions findOptions) {
        return (Objects.nonNull(findOptions)) ?
                this.datastore.find(tClass).get(findOptions) :
                this.datastore.find(tClass).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T findOne(Class<T> tClass, Object id) {
        return this.datastore.createQuery(tClass)
                .field(ID_KEY)
                .equal(id)
                .get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> void delete(T t) {
        this.datastore.delete(t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> void delete(Class<T> tClass, Object id) {
        this.datastore.delete(
                this.datastore.createQuery(tClass)
                .field(ID_KEY)
                .equal(id)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int update(T t, UpdateOperations<T> operations) {
        return this.datastore.update(t, operations).getUpdatedCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int update(Class<T> tClass, Object id, UpdateOperations<T> operations) {
        Query<T> tQuery = this.datastore.createQuery(tClass)
                .field(ID_KEY).equal(id);
        return this.datastore.update(tQuery, operations)
                .getUpdatedCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int updateAll(Class<T> tClass, UpdateOperations<T> operations, Map<String, Object> criteriaMap) {
        Query<T> tQuery = this.datastore.createQuery(tClass);
        if (criteriaMap.size() > 0) {
            criteriaMap.forEach(
                    (key, value) -> {
                        tQuery.field(key)
                                .equal(value);
                    }
            );
        }
        return this.datastore.update(tQuery, operations)
                .getUpdatedCount();
    }

}
