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

package com.adeptj.modules.data.jpa.api;

import java.util.List;
import java.util.Map;

/**
 * JPA Repository for CRUD operations to be performed by the application on underlying DB.
 * All the operations defined in the contract throw JpaSystemException which is a wrapped exception
 * of the actual exception thrown by the PersistenceProvider.
 * <p>
 * <T> is the Entity type which this repository is dealing with. This should always be a subclass of {@link BaseEntity}.
 * Compiler will enforce this as a result of using Java Generics thus enforcing Type Safe JPA operations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface JpaCrudRepository {

    /**
     * Inserts the given JPA entity in DB.
     *
     * @param entity the JPA entity instance
     * @param <T>    type of the JPA entity
     */
    <T extends BaseEntity> void insert(T entity);

    /**
     * Updates the given JPA entity in DB.
     *
     * @param entity the JPA entity instance
     * @param <T>    type of the JPA entity
     * @return Updated JPA entity instance
     */
    <T extends BaseEntity> T update(T entity);

    /**
     * Updates the given JPA entity using the criteria attributes and setting the update attributes.
     *
     * @param entity             the JPA entity class
     * @param criteriaAttributes the mapping of entity attributes on which criteria has to be applied
     *                           and the corresponding values using AND operator
     * @param updateAttributes   the mapping of entity attributes which are to be updated with the
     *                           corresponding values
     * @param <T>                type of the JPA entity
     * @return returns how many rows were affected by the update query
     */
    <T extends BaseEntity> int updateByCriteria(Class<T> entity, Map<String, Object> criteriaAttributes, Map<String, Object> updateAttributes);

    /**
     * Deletes the given JPA entity from DB.
     *
     * @param entity the JPA entity instance
     * @param <T>    type of the JPA entity
     */
    <T extends BaseEntity> void delete(T entity);

    /**
     * Deletes the given JPA entity using the criteria attributes.
     *
     * @param entity        the JPA entity class object
     * @param namedQuery    name of the declared named query
     * @param ordinalParams parameters to set for ?index in the query a.k.a Ordinal Parameters
     * @param <T>           type of the JPA entity
     * @return returns how many rows were deleted
     */
    <T extends BaseEntity> int deleteByNamedQuery(Class<T> entity, String namedQuery, List<Object> ordinalParams);

    /**
     * Deletes the given JPA entity using Criteria API.
     *
     * @param entity             the JPA entity class object
     * @param criteriaAttributes the mapping of entity attributes on which criteria has to be applied
     *                           and the corresponding values using AND operator
     * @param <T>                type of the JPA entity
     * @return returns how many rows were deleted
     */
    <T extends BaseEntity> int deleteByCriteria(Class<T> entity, Map<String, Object> criteriaAttributes);

    /**
     * Deletes all the rows from db of given JPA entity.
     *
     * @param entity the JPA entity class object
     * @param <T>    type of the JPA entity
     * @return returns how many rows were deleted
     */
    <T extends BaseEntity> int deleteAll(Class<T> entity);

    /**
     * Finds the given JPA entity using the primary key.
     *
     * @param entity     the JPA entity class object
     * @param primaryKey the primary key of the JPA entity
     * @param <T>        type of the JPA entity
     */
    <T extends BaseEntity> T findById(Class<T> entity, Object primaryKey);

    /**
     * Finds the given JPA entity using Criteria API.
     *
     * @param entity             the JPA entity class object
     * @param criteriaAttributes the mapping of entity attributes on which criteria has to be applied
     *                           and the corresponding values using AND operator
     * @param <T>                type of the JPA entity
     * @return returns no. of rows found
     */
    <T extends BaseEntity> List<T> findByCriteria(Class<T> entity, Map<String, Object> criteriaAttributes);

    <T extends BaseEntity> List<T> findByCriteria(Class<T> entity, Map<String, Object> namedParams, int startPos, int maxResult);

    <T extends BaseEntity> List<T> findByNamedQuery(Class<T> entity, String namedQuery, List<Object> posParams);

    <T extends BaseEntity> List<T> findAll(Class<T> entity);

    <T extends BaseEntity> List<T> findAll(Class<T> entity, int startPos, int maxResult);

    <T extends BaseEntity> List<T> findByQuery(String jpaQuery, Class<T> entity);

    <T extends BaseEntity> List<T> findByQuery(String jpaQuery, Class<T> entity, int startPos, int maxResult);

    <T extends BaseEntity> List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values);

    <E> E getScalarResultByNamedQuery(Class<E> entity, String namedQuery, List<Object> posParams);

    /**
     * Count the no. of rows of given JPA entity.
     *
     * @param entity the JPA entity class object
     * @param <T>    type of the JPA entity
     * @return count of no. of rows og given JPA entity
     */
    <T extends BaseEntity> Long count(Class<T> entity);

    /**
     * Count the no. of rows of given JPA entity using Criteria.
     *
     * @param entity             the JPA entity class object
     * @param criteriaAttributes the mapping of entity attributes on which criteria has to be applied
     *                           and the corresponding values using AND operator
     * @param <T>                type of the JPA entity
     * @return count of no. of rows og given JPA entity
     */
    <T extends BaseEntity> Long countByCriteria(Class<T> entity, Map<String, Object> criteriaAttributes);

}
