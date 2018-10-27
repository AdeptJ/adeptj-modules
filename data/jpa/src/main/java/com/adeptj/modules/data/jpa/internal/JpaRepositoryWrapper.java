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
import com.adeptj.modules.data.jpa.JpaUtil;
import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import org.eclipse.persistence.jpa.PersistenceProvider;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;

/**
 * Plain wrapper around {@link JpaRepository} and corresponding {@link EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class JpaRepositoryWrapper {

    private final String persistenceUnit;

    private AbstractJpaRepository repository;

    private EntityManagerFactory emf;

    JpaRepositoryWrapper(String persistenceUnit, JpaRepository repository) {
        this.persistenceUnit = persistenceUnit;
        this.repository = (AbstractJpaRepository) repository;
    }

    String getPersistenceUnit() {
        return persistenceUnit;
    }

    AbstractJpaRepository getRepository() {
        return repository;
    }

    void disposeRepository() {
        JpaUtil.close(this.emf);
        this.emf = null;
        if (this.repository != null) {
            this.repository.setEntityManagerFactory(null);
            this.repository = null;
        }
    }

    void initEntityManagerFactory(Map<String, Object> properties) {
        this.emf = new PersistenceProvider().createEntityManagerFactory(this.persistenceUnit, properties);
        JpaUtil.assertInitialized(this.emf);
        EntityManagerFactory emfProxy = (EntityManagerFactory) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{EntityManagerFactory.class},
                new EntityManagerFactoryProxyHandler(this.emf));
        this.repository.setEntityManagerFactory(emfProxy);
    }

    // <------------------ Generated ------------------->

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaRepositoryWrapper that = (JpaRepositoryWrapper) o;
        return Objects.equals(this.persistenceUnit, that.persistenceUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.persistenceUnit);
    }
}
