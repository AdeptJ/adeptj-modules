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

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * Mongo crud operation repository.
 *
 * @author prince.arora, AdeptJ.
 */
public interface MongoCrudRepository {

    /**
     * Returns {@link Datastore} generated while creating connection.
     * @return
     */
    Datastore getDatastore();

    /**
     * Save a document of type <T> in mongodb.
     * <p>
     * {@link Object} will be return after operation, which will represent
     * document id for saved document.
     *
     * @param t  Entity to be inserted in db
     * @param <T>
     * @return  {@link Object} saved document id
     */
    <T extends BaseEntity> Object save(T t);

    /**
     * Returns {@link List} of entities present in database.
     * <p>
     * If {@link FindOptions} ifd provided then it will be used
     * to fetch data from database other wise all entities will be returned.
     *
     * @param tClass   entity class
     * @param findOptions   {@link FindOptions} to fetch data from database
     * @param <T>
     *
     * @return {@link List<T>}  list of entity
     */
    <T extends BaseEntity>List<T> findAll(Class<T> tClass, FindOptions findOptions);

    /**
     * Returns {@link List} of entities matching given criteria.
     * <p>
     * If {@link FindOptions} ifd provided then it will be used
     * to fetch data from database other wise all entities will be returned. And
     * map will be used as and query to fetch data.
     *
     * @param tClass    entity class type
     * @param findOptions   {@link FindOptions} find options for query
     * @param criteriaMap   predicates to be used in query
     * @param <T>
     *
     * @return  {@link List<T>} list of entity
     */
    <T extends BaseEntity>List<T> findAll(Class<T> tClass, FindOptions findOptions, Map<String, Object> criteriaMap);

    /**
     * Returns a document entity found in database.
     * <p>
     * If {@link FindOptions} ifd provided then it will be used to fetch data
     * from database other wise this first one will be returned.
     *
     * @param tClass   entity type class
     * @param findOptions   {@link FindOptions} find options for query
     * @param <T>
     *
     * @return  T   entity
     */
    <T extends BaseEntity> T findOne(Class<T> tClass, FindOptions findOptions);

    /**
     * Returns document found in mongo db fro given id.
     *
     * @param tClass   entity class type
     * @param id   document id
     * @param <T>
     *
     * @return  T   entity
     */
    <T extends BaseEntity> T findOne(Class<T> tClass, Object id);

    /**
     * Delete a document from mongodb.
     *
     * @param t   entity to be deleted from database
     * @param <T>
     */
    <T extends BaseEntity> void delete(T t);

    /**
     * Delete a document from mongo db based on given id.
     *
     * @param tClass   entity type class
     * @param id    document id
     * @param <T>
     */
    <T extends BaseEntity> void delete(Class<T> tClass, Object id);

    /**
     * Update a document in mongo db.
     *
     * @param t    entity to be updated in database
     * @param operations    {@link UpdateOperations} properties to be updated fo document
     * @param <T>
     * @return int  count of updated documents in database.
     */
    <T extends BaseEntity> int update(T t, UpdateOperations<T> operations);

    /**
     * Update a document by its id in database
     *
     * @param tClass   entity class type
     * @param id    document id
     * @param operations   {@link UpdateOperations} properties to be updated fo document
     * @param <T>
     * @return int  count of updated documents in database.
     */
    <T extends BaseEntity> int update(Class<T> tClass, Object id, UpdateOperations<T> operations);

    /**
     * Update all document found in database matching given predicates.
     *
     * @param tClass   entity type class
     * @param operations    {@link UpdateOperations} properties to be updated fo document
     * @param criteriaMap   predicates to be used in query
     * @param <T>
     * @return int  count of updated documents in database.
     */
    <T extends BaseEntity> int updateAll(Class<T> tClass, UpdateOperations<T> operations, Map<String, Object> criteriaMap);

}
