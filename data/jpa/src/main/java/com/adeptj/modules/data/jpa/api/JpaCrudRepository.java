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

import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.ConstructorCriteria;
import com.adeptj.modules.data.jpa.CrudDTO;
import com.adeptj.modules.data.jpa.DeleteCriteria;
import com.adeptj.modules.data.jpa.ReadCriteria;
import com.adeptj.modules.data.jpa.TupleQueryCriteria;
import com.adeptj.modules.data.jpa.UpdateCriteria;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;

/**
 * JPA 2.2 Repository for CRUD operations to be performed by the application on underlying DB.
 * All the operations defined in the contract throw PersistenceException which is a wrapped exception
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
     * @return the entity instance having id assigned from DB
     */
    <T extends BaseEntity> T insert(T entity);

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
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     *                 And the mapping of entity attributes which are to be updated.
     * @param <T>      type of the JPA entity
     * @return returns how many rows were affected by the update query
     */
    <T extends BaseEntity> int updateByCriteria(UpdateCriteria<T> criteria);

    /**
     * Deletes the given JPA entity from DB.
     *
     * @param entity     the JPA entity instance
     * @param primaryKey the primary key of the JPA entity
     * @param <T>        type of the JPA entity
     */
    <T extends BaseEntity> void delete(Class<T> entity, Object primaryKey);

    /**
     * Deletes the given JPA entity using the CrudDTO attributes.
     *
     * @param crudDTO DTO holding the JPA entity class object, name of the declared named query
     *                and parameters to set for ?index in the query a.k.a positional parameters
     * @param <T>     type of the JPA entity
     * @return returns how many rows were deleted
     */
    <T extends BaseEntity> int deleteByJpaNamedQuery(CrudDTO<T> crudDTO);

    /**
     * Deletes the given JPA entity using Criteria API.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     * @param <T>      type of the JPA entity
     * @return returns how many rows were deleted
     */
    <T extends BaseEntity> int deleteByCriteria(DeleteCriteria<T> criteria);

    /**
     * Deletes all the rows from db of given JPA entity.
     * <p>
     * Note: This will delete all the rows from DB, you better know what are you doing.
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
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     * @param <T>      type of the JPA entity
     * @return returns no. of rows found
     */
    <T extends BaseEntity> List<T> findByCriteria(ReadCriteria<T> criteria);

    /**
     * Finds the given JPA entity using Criteria TupleQuery.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     *                 List of parameters to bind to query a.k.a positional parameters
     * @param <T>      type of the JPA entity
     * @return returns no. of rows found
     */
    <T extends BaseEntity> List<Tuple> findByTupleQuery(TupleQueryCriteria<T> criteria);

    /**
     * Finds the given JPA entity using Criteria API. Helpful wherever pagination is required.
     * <p>
     * First the no. of records can be checked using {@link JpaCrudRepository#count(Class)} method
     * and then the pagination query can be fired.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     *                 The position of the first result.
     *                 The maximum number of results to retrieve.
     * @param <T>      type of the JPA entity
     * @return returns no. of rows found
     */
    <T extends BaseEntity> List<T> findPaginatedRecordsByCriteria(ReadCriteria<T> criteria);

    /**
     * Finds the given JPA entity using the named JPA query.
     *
     * @param resultClass the type of the query result.
     * @param namedQuery  The name of a query defined in metadata in JPQL format.
     * @param posParams   And list of parameters to bind to query a.k.a positional parameters
     * @param <T>         type of the record that is to be return
     * @return List of entity found by named query execution
     */
    <T> List<T> findByJpaNamedQuery(Class<T> resultClass, String namedQuery, List<Object> posParams);

    /**
     * Finds the entity instances of given type using query specified in JPQL format.
     *
     * @param namedQuery named query either JPQL or native
     * @param posParams  List of parameters to bind to query a.k.a positional parameters
     * @return List of entity or Object[] found by JPA query(JPQL format) or native query execution
     */
    <T> List<T> findByNamedQuery(String namedQuery, List<Object> posParams);

    /**
     * Finds all entities of given type.
     * <p>
     * Note: Should never be called for large number of rows as you don't want millions of row in memory.
     *
     * @param entity the JPA entity class object
     * @param <T>    type of the JPA entity
     * @return All of the rows(Entity instances)
     */
    <T extends BaseEntity> List<T> findAll(Class<T> entity);

    /**
     * Finds all entities of given type. Helpful wherever pagination is required.
     * <p>
     * Note: Should never be called for large number of rows.
     * <p>
     * First the no. of records can be checked using {@link JpaCrudRepository#count(Class)} method
     * and then the pagination query can be fired.
     *
     * @param entity    the JPA entity class object
     * @param startPos  position of the first result
     * @param maxResult maximum number of results to retrieve
     * @param <T>       type of the JPA entity
     * @return All of the rows(Entity instances)
     */
    <T extends BaseEntity> List<T> findPaginatedRecords(Class<T> entity, int startPos, int maxResult);

    /**
     * Finds the entity instances of given type using query specified in JPQL format.
     *
     * @param crudDTO DTO holding the JPA entity class object.
     *                The query in JPQL format
     *                The list of parameters to bind to query a.k.a positional parameters
     * @param <T>     type of the JPA entity
     * @return List of entity found by JPA query(JPQL format) execution
     */
    <T extends BaseEntity> List<T> findByJpaQuery(CrudDTO<T> crudDTO);

    /**
     * Finds the entity instances of given type using JPQL with start and max result option.
     *
     * @param crudDTO DTO holding the JPA entity class object.
     *                The query in JPQL format
     *                The list of parameters to bind to query a.k.a positional parameters
     *                The position of the first result.
     *                The maximum number of results to retrieve.
     * @param <T>     type of the JPA entity
     * @return List of entity found by JPA query(JPQL format) execution
     */
    <T extends BaseEntity> List<T> findPaginatedRecordsByJpaQuery(CrudDTO<T> crudDTO);

    /**
     * Find the given entity using SQL IN operator
     *
     * @param entity        the JPA entity class object
     * @param attributeName entity attribute against which IN has to be applied
     * @param values        values on which IN has to be applied
     * @param <T>           type of the JPA entity
     * @return List of entity found by criteria
     */
    <T extends BaseEntity> List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values);

    /**
     * Finds the entity using given named native query and project in the given result class.
     *
     * @param resultClass the type of the query result
     * @param nativeSql   the native sql
     * @param mapping     the name of the result set mapping
     * @param posParams   List of parameters to bind to query a.k.a positional parameters
     * @param <T>         Type of returned instance
     * @return singular result from query execution
     */
    <T> List<T> findAndMapResultSet(Class<T> resultClass, String nativeSql, String mapping, List<Object> posParams);

    /**
     * First find the entity using the given Jpa query and then Map the result to the constructor
     * of type specified as resultClass.
     *
     * @param resultClass the type of the Constructor being mapped
     * @param jpaQuery    query in JPQL format
     * @param posParams   List of parameters to bind to query a.k.a positional parameters
     * @param <T>         Type of returned instance
     * @return List of instances of type as resultClass
     */
    <T> List<T> findAndMapConstructor(Class<T> resultClass, String jpaQuery, List<Object> posParams);

    /**
     * First find the entity using the Jpa criteria and then Map the result to the constructor
     * of type specified by type parameter C.
     *
     * @param criteria Object holding the JPA entity class object.
     *                 Constructor class object.
     *                 Selection of attributes, same as number of parameters of Constructor.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     * @param <T>      JPA entity type
     * @param <C>      Constructor type
     * @return List of instances of type specified by type parameter C
     */
    <T extends BaseEntity, C> List<C> findAndMapConstructor(ConstructorCriteria<T, C> criteria);

    /**
     * Gets the single result against the named query which must be in JPQL format.
     *
     * @param resultClass the type of the query result
     * @param namedQuery  the name of a query defined in metadata
     * @param posParams   List of parameters to bind to query a.k.a positional parameters
     * @param <T>         Type of returned instance
     * @return singular result from query execution
     */
    <T extends BaseEntity> T getEntity(Class<T> resultClass, String namedQuery, List<Object> posParams);

    /**
     * Count the no. of rows of given JPA entity.
     *
     * @param entity the JPA entity class object
     * @param <T>    type of the JPA entity
     * @return count of no. of rows og given JPA entity
     */
    <T extends BaseEntity> Long count(Class<T> entity);

    /**
     * Count the no. of rows of given JPA entity using UpdateCriteria.
     *
     * @param entity             the JPA entity class object
     * @param criteriaAttributes the mapping of entity attributes on which criteria has to be applied
     *                           and the corresponding values using AND operator
     * @param <T>                type of the JPA entity
     * @return count of no. of rows og given JPA entity
     */
    <T extends BaseEntity> Long countByCriteria(Class<T> entity, Map<String, Object> criteriaAttributes);

    /**
     * Gets the {@link EntityManager}
     * <p>
     * Note: Caller should not maintain a reference of returned EntityManager instance.
     * And it is the responsibility of caller to close the {@link EntityManager} to prevent
     * resource leakage to happen.
     *
     * @return The {@link EntityManager} instance
     */
    EntityManager getEntityManager();
}
