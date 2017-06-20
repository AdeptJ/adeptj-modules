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
 *
 * <T> is the Entity type which this repository is dealing with. This should always be a subclass of {@link BaseEntity}.
 * Compiler will enforce this as a result of using Java Generics thus enforcing Type Safe JPA operations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface JpaCrudRepository {

    <T extends BaseEntity> T insert(T entity);

    <T extends BaseEntity> T update(T entity);

    <T extends BaseEntity> List<T> findByCriteria(Class<T> criteriaClass, Map<String, Object> queryParams);

    <T extends BaseEntity> List<T> findByNamedQuery(Class<T> criteriaClass, String namedQuery, Object... queryParams);

    <E> E getScalarResultByNamedQuery(Class<E> scalarResultClass, String namedQuery, Object... queryParams);

    <T extends BaseEntity> List<T> findAll(Class<T> entityClass);

    <T extends BaseEntity> void delete(T entity);

    <T extends BaseEntity> int deleteByNamedQuery(Class<T> entityClass, String namedQuery, Object... queryParams);

    <T extends BaseEntity> int deleteByCriteria(Class<T> entityClass, Map<String, Object> predicateMap);

    <T extends BaseEntity> int deleteAll(Class<T> entityClass);

    <T extends BaseEntity> int updateByCriteria(Class<T> entityClass, Map<String, Object> predicateMap, Map<String, Object> updateFields);

    <T extends BaseEntity> List<T> findByCriteriaWithINParams(Map<String, List<Object>> inParams, Class<T> criteriaClass);

    <T extends BaseEntity> List<T> findAll(Class<T> entityClass, int startIndex, int limit);

    <T extends BaseEntity> Long count(Class<T> entityClass);

    <T extends BaseEntity> int countByCriteria(Class<T> entityClass, Map<String, Object> queryParams);

    <T extends BaseEntity> List<T> findByCriteria(Class<T> criteriaClass, Map<String, Object> queryParams, int startIndex, int limit);

    <T extends BaseEntity> List<T> selectByQuery(String query, Class<T> entityClass);

    <T extends BaseEntity> List<T> selectByQuery(String query, Class<T> entityClass, int startIndex, int limit);

}
