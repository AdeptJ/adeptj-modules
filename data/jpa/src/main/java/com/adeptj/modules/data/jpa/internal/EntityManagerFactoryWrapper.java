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

import javax.persistence.Cache;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * A simple wrapper for {@link EntityManagerFactory}, this way {@link EntityManagerFactory} object is not directly exposed
 * which further prevents the possibility of calling {@link EntityManagerFactory#close()} by consumers.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class EntityManagerFactoryWrapper implements EntityManagerFactory {

    private EntityManagerFactory delegate;

    EntityManagerFactoryWrapper(EntityManagerFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public EntityManager createEntityManager() {
        return this.delegate.createEntityManager();
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        return this.delegate.createEntityManager(map);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return this.delegate.createEntityManager(synchronizationType);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return this.delegate.createEntityManager(synchronizationType, map);
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return this.delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return this.delegate.getMetamodel();
    }

    @Override
    public boolean isOpen() {
        return this.delegate.isOpen();
    }

    @Override
    public void close() {
        throw new IllegalStateException("Managed EntityManagerFactory can't be closed by consumer code!!");
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.delegate.getProperties();
    }

    @Override
    public Cache getCache() {
        return this.delegate.getCache();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.delegate.getPersistenceUnitUtil();
    }

    @Override
    public void addNamedQuery(String name, Query query) {
        this.delegate.addNamedQuery(name, query);
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return this.delegate.unwrap(cls);
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        this.delegate.addNamedEntityGraph(graphName, entityGraph);
    }
}
