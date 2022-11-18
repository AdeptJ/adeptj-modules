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

import jakarta.persistence.EntityManager;

/**
 * This is a functional interface and can therefore be used as the assignment target for a lambda expression or method reference.
 * <p>
 * Callback interface for JPA code. To be used with {@link JpaRepository#executeCallback} method.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@FunctionalInterface
public interface JpaCallback<T> {

    /**
     * Gets called by {@link JpaRepository#executeCallback} with an active JPA EntityManager.
     * <p>
     * Caller does not need to care about activating or closing the EntityManager, or handling transactions.
     *
     * @param em an active EntityManager
     * @return a result object, or null if none
     * @throws com.adeptj.modules.data.jpa.exception.JpaException if something goes wrong.
     */
    T doInJpa(EntityManager em);
}
