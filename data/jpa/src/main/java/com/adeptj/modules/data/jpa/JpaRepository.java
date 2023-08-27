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
import com.adeptj.modules.data.jpa.query.InParam;
import com.adeptj.modules.data.jpa.query.OutParam;
import com.adeptj.modules.data.jpa.query.QueryParam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import org.osgi.annotation.versioning.ConsumerType;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * JPA 3.1 Repository for CRUD operations to be performed by the application on underlying DB.
 * All the operations defined in the contract throw JpaException which is a wrapped exception
 * of the actual exception thrown by the PersistenceProvider.
 * <p>
 *
 * @param <T>  The {@link BaseEntity} subclass type which this repository is dealing with.
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
     * Deletes the given JPA entity from DB.
     *
     * @param entity     the JPA entity instance
     * @param primaryKey the primary key of the JPA entity
     */
    void delete(Class<T> entity, ID primaryKey);

    /**
     * Deletes the rows from DB by executing the JPA named query.
     *
     * @param name   The name of a query defined in metadata.
     * @param params And list of parameters to bind to query parameters (named or positional)
     * @return returns how many rows were deleted
     */
    int deleteByJpaNamedQuery(String name, QueryParam... params);

    /**
     * Deletes the given JPA entity using Criteria API.
     *
     * @param criteria Object composed of the JPA entity class.
     *                 The mapping of entity attributes on which criteria has to be applied using AND operator.
     * @return returns how many rows were deleted
     */
    int deleteByCriteria(DeleteCriteria<T> criteria);

    /**
     * Deletes all the rows of given JPA entity in DB.
     * <p>
     * Note: This will delete all the rows in DB, you better know what are you doing.
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
     *                 List of parameters to bind to query parameters (named or positional)
     * @return returns no. of rows found
     */
    List<Tuple> findByTupleCriteria(TupleCriteria<T> criteria);

    /**
     * Finds all the values of the entity attribute(DB column).
     * <p>
     * Note: Should never be called for large number of rows as you may not want all of them in memory.
     *
     * @param entity        the JPA entity class object.
     * @param attributeName the name of the attribute present in the JPA entity.
     * @param attributeType the Java type of the attribute present in the JPA entity.
     * @param <E>           the result Java type.
     * @return all the rows of the selected DB column.
     */
    <E> List<E> findAttributeValuesByCriteria(Class<T> entity, String attributeName, Class<E> attributeType);

    /**
     * Finds all the values of the entity attributes(DB columns).
     * <p>
     * Note: Should never be called for large number of rows as you may not want all of them in memory.
     *
     * @param entity         the JPA entity class object.
     * @param attributeNames the names of the attributes present in the JPA entity.
     * @return all the rows of the selected DB columns.
     */
    List<Object[]> findMultiAttributeValuesByCriteria(Class<T> entity, String... attributeNames);

    /**
     * Finds the given JPA entity using the named JPA query.
     *
     * @param resultClass the type of the query result.
     * @param name        The name of a query defined in metadata.
     * @param params      And list of parameters to bind to query parameters (named or positional)
     * @return List of entity found by named query execution
     */
    <E> List<E> findByNamedQuery(Class<E> resultClass, String name, QueryParam... params);

    /**
     * Finds all entities of given type.
     * <p>
     * Note: Should never be called for large number of rows as you may not want all of them in memory.
     *
     * @param entity the JPA entity class object
     * @return All the rows(Entity instances)
     */
    List<T> findAll(Class<T> entity);

    /**
     * Find the given entity using SQL IN operator
     *
     * @param entity        the JPA entity class object
     * @param attributeName entity attribute against which IN has to be applied
     * @param values        values on which IN has to be applied
     * @param negation      apply negation (NOT IN) if this argument is true
     * @return List of entity found by criteria
     */
    List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values, boolean negation);

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
    <C> List<C> findByCriteriaWithDTOProjection(ConstructorCriteria<T, C> criteria);

    /**
     * Gets the single result against the named query.
     *
     * @param resultClass the type of the query result
     * @param name        the name of a query defined in metadata
     * @param params      List of parameters to bind to query parameters (named or positional)
     * @param <E>         Type of returned instance
     * @return singular result begin query execution
     */
    <E> E findSingleResultByNamedQuery(Class<E> resultClass, String name, QueryParam... params);

    /**
     * Count the no. of rows of given JPA entity.
     *
     * @param entity the JPA entity class object
     * @return count of no. of rows of given JPA entity
     */
    Long countByCriteria(Class<T> entity);

    /**
     * Count the no. of rows of given JPA entity.
     *
     * @param name the name of a query defined in metadata.
     * @return count of no. of rows of given JPA entity
     */
    Long countByNamedQuery(String name);

    /**
     * Use the {@link Function} for any specific operation on {@link EntityManager}.
     *
     * @param function    the function which takes {@link EntityManager} as argument.
     * @param requiresTxn whether an entity transaction is needed
     * @param <E>         the result type
     * @return a result object returned by the action, or null
     */
    <E> E doWithEntityManager(Function<EntityManager, E> function, boolean requiresTxn);

    // <<------------------------ StoredProcedure Support ------------------------>>

    /**
     * Execute a named stored procedure defined in metadata or as an annotation on an entity.
     *
     * @param name         the name of the stored procedure defined in metadata or as an annotation on an entity
     * @param params       the IN parameters
     * @param outParamName the OUT parameter name
     * @return result of the stored procedure execution.
     */
    Object executeNamedStoredProcedure(String name, List<InParam> params, String outParamName);

    /**
     * Directly execute a stored procedure from DB.
     *
     * @param procedureName the name of the stored procedure in DB
     * @param params        the IN parameters
     * @param outParam      the {@link OutParam} object containing name and type info
     * @return result of the stored procedure execution.
     */
    Object executeStoredProcedure(String procedureName, List<InParam> params, OutParam outParam);

    /**
     * Find all the records of the type (resultClasses) declared in metadata or annotation on an entity
     *
     * @param name   the name of the stored procedure defined in metadata or as an annotation on an entity
     * @param params the IN parameters
     * @param <E>    the result type
     * @return List of instances of type (resultClasses) declared in metadata or annotation on an entity
     */
    <E> List<E> findByNamedStoredProcedure(String name, InParam... params);

    /**
     * Find all the records of the type specified by type parameter E
     *
     * @param resultClass   the type of result the procedure call returns.
     * @param procedureName the name of the stored procedure in DB
     * @param params        the IN parameters
     * @param <E>           the result type
     * @return List of instances of type resultClass
     */
    <E> List<E> findByStoredProcedure(Class<E> resultClass, String procedureName, InParam... params);
}
