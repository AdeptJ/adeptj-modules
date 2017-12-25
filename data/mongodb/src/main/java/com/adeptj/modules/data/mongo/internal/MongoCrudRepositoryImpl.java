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
import com.mongodb.client.result.UpdateResult;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.adeptj.modules.data.mongo.api.MongoConstants.KEY_ID;

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

    @Override
    public Datastore getDatastore() {
        return this.datastore;
    }

    @Override
    public <T extends BaseEntity> Object save(T t) {
        return this.datastore.save(t).getId();
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> tClass, FindOptions findOptions) {
        return (Objects.nonNull(findOptions)) ?
                this.datastore.find(tClass).asList(findOptions) :
                this.datastore.find(tClass).asList();

    }

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

    @Override
    public <T extends BaseEntity> T findOne(Class<T> tClass, FindOptions findOptions) {
        return (Objects.nonNull(findOptions)) ?
                this.datastore.find(tClass).get(findOptions) :
                this.datastore.find(tClass).get();
    }

    @Override
    public <T extends BaseEntity> T findOne(Class<T> tClass, Object id) {
        return this.datastore.createQuery(tClass)
                .field(KEY_ID)
                .equal(id)
                .get();
    }

    @Override
    public <T extends BaseEntity> void delete(T t) {
        this.datastore.delete(t);
    }

    @Override
    public <T extends BaseEntity> void delete(Class<T> tClass, Object id) {
        this.datastore.delete(
                this.datastore.createQuery(tClass)
                .field(KEY_ID)
                .equal(id)
        );
    }

    @Override
    public <T extends BaseEntity> Object update(T t, UpdateOperations<T> operations) {
        return this.datastore.update(t, operations)
                .getNewId();
    }

    @Override
    public <T extends BaseEntity> int update(Class<T> tClass, Object id, UpdateOperations<T> operations) {
        Query<T> tQuery = this.datastore.createQuery(tClass)
                .field(KEY_ID).equal(id);
        return this.datastore.update(tQuery, operations)
                .getUpdatedCount();
    }

}
