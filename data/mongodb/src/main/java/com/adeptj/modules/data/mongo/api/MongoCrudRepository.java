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

    Datastore getDatastore();

    <T extends BaseEntity> Object save(T t);

    <T extends BaseEntity>List<T> findAll(Class<T> tClass, FindOptions findOptions);

    <T extends BaseEntity>List<T> findAll(Class<T> tClass, FindOptions findOptions, Map<String, Object> criteriaMap);

    <T extends BaseEntity> T findOne(Class<T> tClass, FindOptions findOptions);

    <T extends BaseEntity> T findOne(Class<T> tClass, Object id);

    <T extends BaseEntity> void delete(T t);

    <T extends BaseEntity> void delete(Class<T> tClass, Object id);

    <T extends BaseEntity> Object update(T t, UpdateOperations<T> operations);

    <T extends BaseEntity> int update(Class<T> tClass, Object id, UpdateOperations<T> operations);

}
