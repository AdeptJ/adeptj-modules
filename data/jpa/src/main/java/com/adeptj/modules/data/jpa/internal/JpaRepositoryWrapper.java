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

package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.JpaRepository;

import javax.persistence.EntityManagerFactory;
import java.util.Objects;

/**
 * Plain wrapper around {@link JpaRepository} and corresponding {@link EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class JpaRepositoryWrapper {

    private final String persistenceUnitName;

    private JpaRepository repository;

    private EntityManagerFactory entityManagerFactory;

    JpaRepositoryWrapper(String persistenceUnitName, JpaRepository repository) {
        this.persistenceUnitName = persistenceUnitName;
        this.repository = repository;
    }

    String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    JpaRepository getRepository() {
        return repository;
    }

    void unsetRepository() {
        this.repository = null;
    }

    EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    // <------------------ Generated ------------------->

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaRepositoryWrapper that = (JpaRepositoryWrapper) o;
        return Objects.equals(this.persistenceUnitName, that.persistenceUnitName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.persistenceUnitName);
    }
}
