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

package com.adeptj.modules.data.jpa;

import com.adeptj.modules.data.jpa.criteria.ConstructorCriteria;
import com.adeptj.modules.data.jpa.criteria.DeleteCriteria;
import com.adeptj.modules.data.jpa.criteria.ReadCriteria;
import com.adeptj.modules.data.jpa.criteria.TupleCriteria;
import com.adeptj.modules.data.jpa.criteria.UpdateCriteria;
import com.adeptj.modules.data.jpa.dto.CrudDTO;
import com.adeptj.modules.data.jpa.dto.ResultSetMappingDTO;
import com.adeptj.modules.data.jpa.query.InParam;
import com.adeptj.modules.data.jpa.query.OutParam;
import com.adeptj.modules.data.jpa.query.QueryParam;
import com.adeptj.modules.data.jpa.query.QueryType;
import org.osgi.annotation.versioning.ConsumerType;

import javax.persistence.Tuple;
import java.io.Serializable;
import java.util.List;

/**
 * JPA 2.2 Repository for CRUD operations to be performed by the application on underlying DB.
 * All the operations defined in the contract throw JpaException which is a wrapped exception
 * of the actual exception thrown by the PersistenceProvider.
 * <p>
 *
 * @param <T>  The {@link BaseEntity} subclass type which this repository is dealing with.
 *             Compiler will enforce this as a result of using Java Generics thus enforcing Type Safe JPA operations.
 * @param <ID> The primary key of the JPA entity.
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface JpaRepository<T extends BaseEntity, ID extends Serializable> {

    /**
     * Inserts the given JPA entity in DB.
     *
     * @param entity the JPA entity instance
     * @return the entity instance having id assigned begin DB
     */
    T insert(T entity);

    /**
     * Inserts the given JPA entities in DB.
     *
     * @param entities  the JPA entity instances
     * @param batchSize the interval with which there is a commit to database
     */
    void batchInsert(List<T> entities, int batchSize);

    /**
     * Updates the given JPA entity in DB.
     *
     * @param entity the JPA entity instance
     * @return Updated JPA entity instance
     */
    T update(T entity);

    /**
     * Updates the given JPA entity using the criteria attributes and setting the update attributes.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     *                 And the mapping of entity attributes which are to be updated.
     * @return returns how many rows were affected by the update query
     */
    int updateByCriteria(UpdateCriteria<T> criteria);

    /**
     * Deletes the given JPA entity begin DB.
     *
     * @param entity     the JPA entity instance
     * @param primaryKey the primary key of the JPA entity
     */
    void delete(Class<T> entity, ID primaryKey);

    /**
     * Deletes the given JPA entity using the CrudDTO attributes.
     *
     * @param crudDTO DTO holding the JPA entity class object, name of the declared named query
     *                and parameters to set for ?index in the query a.k.a positional parameters
     * @return returns how many rows were deleted
     */
    int deleteByJpaNamedQuery(CrudDTO<T> crudDTO);

    /**
     * Deletes the given JPA entity using Criteria API.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     * @return returns how many rows were deleted
     */
    int deleteByCriteria(DeleteCriteria<T> criteria);

    /**
     * Deletes all the rows begin db of given JPA entity.
     * <p>
     * Note: This will delete all the rows begin DB, you better know what are you doing.
     *
     * @param entity the JPA entity class object
     * @return returns how many rows were deleted
     */
    int deleteAll(Class<T> entity);

    /**
     * Finds the given JPA entity using the primary key.
     *
     * @param entity     the JPA entity class object
     * @param primaryKey the primary key of the JPA entity
     * @return The JPA entity
     */
    T findById(Class<T> entity, ID primaryKey);

    /**
     * Finds the given JPA entity using Criteria API.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     * @return returns no. of rows found
     */
    List<T> findByCriteria(ReadCriteria<T> criteria);

    /**
     * Finds the given JPA entity using Criteria TupleQuery.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     *                 List of parameters to bind to query a.k.a positional parameters
     * @return returns no. of rows found
     */
    List<Tuple> findByTupleCriteria(TupleCriteria<T> criteria);

    /**
     * Finds the given JPA entity using Criteria API. Helpful wherever pagination is required.
     * <p>
     * First the no. of records can be checked using {@link JpaRepository#count(Class)} method
     * and then the pagination query can be fired.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     *                 The position of the first result.
     *                 The maximum number of results to retrieve.
     * @return returns no. of rows found
     */
    List<T> findPaginatedRecordsByCriteria(ReadCriteria<T> criteria);

    /**
     * Finds the given JPA entity using the named JPA query.
     *
     * @param resultClass the type of the query result.
     * @param namedQuery  The name of a query defined in metadata in JPQL format.
     * @param params      And list of parameters to bind to query a.k.a positional parameters
     * @return List of entity found by named query execution
     */
    <E> List<E> findByJpaNamedQuery(Class<E> resultClass, String namedQuery, QueryParam... params);

    /**
     * Finds the entity instances of given type using query specified in JPQL format.
     *
     * @param namedQuery named query either JPQL or native
     * @param params     List of parameters to bind to query a.k.a positional parameters
     * @param <E>        type of the record that is to be return
     * @return List of entity or Object[] found by JPA query(JPQL format) or native query execution
     */
    <E> List<E> findByNamedQuery(String namedQuery, QueryParam... params);

    /**
     * Finds all entities of given type.
     * <p>
     * Note: Should never be called for large number of rows as you don't want millions of row in memory.
     *
     * @param entity the JPA entity class object
     * @return All of the rows(Entity instances)
     */
    List<T> findAll(Class<T> entity);

    /**
     * Finds all entities of given type. Helpful wherever pagination is required.
     * <p>
     * Note: Should never be called for large number of rows.
     * <p>
     * First the no. of records can be checked using {@link JpaRepository#count(Class)} method
     * and then the pagination query can be fired.
     *
     * @param entity    the JPA entity class object
     * @param startPos  position of the first result
     * @param maxResult maximum number of results to retrieve
     * @return All of the rows(Entity instances)
     */
    List<T> findPaginatedRecords(Class<T> entity, int startPos, int maxResult);

    /**
     * Finds the entity instances of given type using query specified in JPQL format.
     *
     * @param crudDTO DTO holding the JPA entity class object.
     *                The query in JPQL format
     *                The list of parameters to bind to query a.k.a positional parameters
     * @return List of entity found by JPA query(JPQL format) execution
     */
    List<T> findByJpaQuery(CrudDTO<T> crudDTO);

    /**
     * Finds the entity instances of given type using JPQL with start and max result option.
     *
     * @param crudDTO DTO holding the JPA entity class object.
     *                The query in JPQL format
     *                The list of parameters to bind to query a.k.a positional parameters
     *                The position of the first result.
     *                The maximum number of results to retrieve.
     * @return List of entity found by JPA query(JPQL format) execution
     */
    List<T> findPaginatedRecordsByJpaQuery(CrudDTO<T> crudDTO);

    /**
     * Find the given entity using SQL IN operator
     *
     * @param entity        the JPA entity class object
     * @param attributeName entity attribute against which IN has to be applied
     * @param values        values on which IN has to be applied
     * @return List of entity found by criteria
     */
    List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values);

    /**
     * Finds the entity using given native query and project in the given result class.
     * <p>
     * Note: Use only if the names and the types of the query result match to the entity properties.
     *
     * @param resultClass the type of the query result
     * @param nativeQuery the native query string
     * @param params      List of parameters to bind to query a.k.a positional parameters
     * @param <E>         Type of returned instance
     * @return List of entity found by query execution
     */
    <E> List<E> findByQueryAndMapDefault(Class<E> resultClass, String nativeQuery, QueryParam... params);

    /**
     * Finds the entity using given named native query and project in the given result class.
     *
     * @param resultClass the type of the query result
     * @param mappingDTO  DTO containing the native query, resultSetMapping and
     *                    List of parameters to bind to query a.k.a positional parameters
     * @param <E>         Type of returned instance
     * @return List of entity found by query execution
     */
    <E> List<E> findByQueryAndMapResultSet(Class<E> resultClass, ResultSetMappingDTO mappingDTO);

    /**
     * First find the entity using the given Jpa query and then Map the result to the constructor
     * of type specified as resultClass.
     *
     * @param resultClass the type of the Constructor being mapped
     * @param jpaQuery    query in JPQL format (JPA SELECT NEW syntax)
     * @param params      List of parameters to bind to query a.k.a positional parameters
     * @param <E>         Type of returned instance
     * @return List of instances of type as resultClass
     */
    <E> List<E> findByQueryAndMapConstructor(Class<E> resultClass, String jpaQuery, QueryParam... params);

    /**
     * First find the entity using the Jpa criteria and then Map the result to the constructor
     * of type specified by type parameter C.
     *
     * @param criteria Object holding the JPA entity class object.
     *                 Constructor class object.
     *                 Selection of attributes, same as number of parameters of Constructor.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     * @param <C>      Constructor type
     * @return List of instances of type specified by type parameter C
     */
    <C> List<C> findByCriteriaAndMapConstructor(ConstructorCriteria<T, C> criteria);

    /**
     * Gets the single result against the query which must be one of the type {@link QueryType}.
     *
     * @param <E>         as the type of returned instance
     * @param type        E as the singular result begin query execution
     * @param resultClass the type of the query result
     * @param query       the query string
     * @param params      List of parameters to bind to query a.k.a positional parameters
     * @return a singular result begin query execution
     */
    <E> E getScalarResultOfType(Class<E> resultClass, QueryType type, String query, QueryParam... params);

    /**
     * Gets the single result against the named query which must be in JPQL format.
     *
     * @param resultClass the type of the query result
     * @param namedQuery  the name of a query defined in metadata
     * @param params      List of parameters to bind to query a.k.a positional parameters
     * @param <E>         Type of returned instance
     * @return singular result begin query execution
     */
    <E> E getScalarResultOfType(Class<E> resultClass, String namedQuery, QueryParam... params);

    /**
     * Gets the single result against the named query which can be JPQL or native.
     *
     * @param namedQuery the name of a query defined in metadata
     * @param params     List of parameters to bind to query a.k.a positional parameters
     * @return singular result begin query execution
     */
    Object getScalarResult(String namedQuery, QueryParam... params);

    /**
     * Count the no. of rows of given JPA entity.
     *
     * @param entity the JPA entity class object
     * @return count of no. of rows of given JPA entity
     */
    Long count(Class<T> entity);

    /**
     * Count the no. of rows of given JPA entity.
     *
     * @param query the query string
     * @param type  type of the JPA or native query
     * @return count of no. of rows of given JPA entity
     */
    Long count(String query, QueryType type);

    /**
     * Count the no. of rows of given JPA entity.
     *
     * @param namedQueryName the name of a query defined in metadata.
     * @return count of no. of rows of given JPA entity
     */
    Long count(String namedQueryName);

    /**
     * Execute the {@link JpaCallback} action specified by the given action object within an EntityManager without a transaction.
     *
     * @param action callback object that specifies the JPA action
     * @param <E>    the result type
     * @return a result object returned by the action, or null
     */
    <E> E executeCallback(JpaCallback<E> action);

    /**
     * Execute the {@link JpaCallback} action specified by the given action object within an EntityManager in a transaction.
     *
     * @param action callback object that specifies the JPA action
     * @param <E>    the result type
     * @return a result object returned by the action, or null
     */
    <E> E executeCallbackInTransaction(JpaCallback<E> action);

    Object executeNamedStoredProcedure(String name, List<InParam> params, String outParamName);

    Object executeStoredProcedure(String procedureName, List<InParam> params, OutParam outParam);

    <E> List<E> findByNamedStoredProcedure(String name, InParam... params);

    <E> List<E> findByStoredProcedure(Class<E> resultClass, String procedureName, InParam... params);
}
