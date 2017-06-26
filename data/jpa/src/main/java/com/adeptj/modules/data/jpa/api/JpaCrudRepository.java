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

    <T extends BaseEntity> void insert(T entity);

    <T extends BaseEntity> T update(T entity);

    <T extends BaseEntity> int updateByCriteria(Class<T> entity, Map<String, Object> namedParams, Map<String, Object> updateFields);

    <T extends BaseEntity> void delete(T entity);

    <T extends BaseEntity> int deleteByNamedQuery(Class<T> entity, String namedQuery, List<Object> posParams);

    <T extends BaseEntity> int deleteByCriteria(Class<T> entity, Map<String, Object> namedParams);

    <T extends BaseEntity> int deleteAll(Class<T> entity);

    <T extends BaseEntity> List<T> findByCriteria(Class<T> entity, Map<String, Object> namedParams);

    <T extends BaseEntity> List<T> findByCriteria(Class<T> entity, Map<String, Object> namedParams, int startPos, int maxResult);

    <T extends BaseEntity> List<T> findByNamedQuery(Class<T> entity, String namedQuery, List<Object> posParams);

    <T extends BaseEntity> List<T> findAll(Class<T> entity);

    <T extends BaseEntity> List<T> findAll(Class<T> entity, int startPos, int maxResult);

    <T extends BaseEntity> List<T> findByQuery(String jpaQuery, Class<T> entity);

    <T extends BaseEntity> List<T> findByQuery(String jpaQuery, Class<T> entity, int startPos, int maxResult);

    <T extends BaseEntity> List<T> findByCriteriaWithINParams(Map<String, List<Object>> inParams, Class<T> entity);

    <E> E getScalarResultByNamedQuery(Class<E> entity, String namedQuery, List<Object> posParams);

    <T extends BaseEntity> Long count(Class<T> entity);

    <T extends BaseEntity> Long countByCriteria(Class<T> entity, Map<String, Object> namedParams);

}
