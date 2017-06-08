/*
 * =============================================================================
 *
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * =============================================================================
 */
package com.adeptj.modules.data.jpa;

import java.util.List;
import java.util.Map;

/**
 * JPA Persistence methods.
 *
 * @author prince.arora, AdeptJ
 */
public interface JPAPersistenceService {

    /**
     * Insert a given entity in database.
     *
     * @param transientObj
     * @param <T>
     * @return
     */
    <T> T insert(T transientObj);

    /**
     * Update a givent entity in database.
     *
     * @param persistentObj
     * @param <T>
     * @return
     */
    <T> T update(T persistentObj);

    /**
     * Find an entity by criteria.
     *
     * @param criteriaClass
     * @param queryParams
     * @param <T>
     * @return
     */
    <T> List<T> findByCriteria(Class<T> criteriaClass, Map<String, Object> queryParams);

    /**
     * find by named query.
     *
     * @param criteriaClass
     * @param namedQuery
     * @param queryParams
     * @param <T>
     * @return
     */
    <T> List<T> findByNamedQuery(Class<T> criteriaClass, String namedQuery, Object... queryParams);

    /**
     * Find all entities.
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    <T> List<T> findAll(Class<T> entityClass);

    /**
     * Delete an entity form Database.
     *
     * @param entityInstance
     * @param <T>
     */
    <T> void delete(T entityInstance);

    /**
     * Delete by criteria.
     *
     * @param entity
     * @param predicateMap
     * @param <T>
     * @return
     */
    <T> int deleteByCriteria(Class<T> entity, Map<String, Object> predicateMap);

}
