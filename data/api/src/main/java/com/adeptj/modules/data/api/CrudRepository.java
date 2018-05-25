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

package com.adeptj.modules.data.api;

import java.util.Optional;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public interface CrudRepository {

    <T> void save(T entity);

    <T> void saveAll(Iterable<T> entities);

    <T> Optional<T> findById(Object id);

    boolean existsById(Object id);

    <T> Iterable<T> findAll();

    <T> Iterable<T> findAllByIds(Iterable<Object> ids);

    long count();

    void deleteById(Object id);

    <T> void delete(T entity);

    <T> void deleteAll(Iterable<? extends T> entities);

    void deleteAll();
}
