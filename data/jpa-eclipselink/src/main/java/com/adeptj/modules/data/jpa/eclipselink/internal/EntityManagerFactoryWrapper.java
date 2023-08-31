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

package com.adeptj.modules.data.jpa.eclipselink.internal;

import jakarta.persistence.Cache;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.Query;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A simple wrapper for {@link EntityManagerFactory}, this way EntityManagerFactory object isn't directly
 * exposed, which prevents the possibility of closing a managed EntityManagerFactory by consumer.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class EntityManagerFactoryWrapper implements EntityManagerFactory {

    private final EntityManagerFactory entityManagerFactory;

    EntityManagerFactoryWrapper(@NotNull EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager createEntityManager() {
        return this.entityManagerFactory.createEntityManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager createEntityManager(Map map) {
        return this.entityManagerFactory.createEntityManager(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return this.entityManagerFactory.createEntityManager(synchronizationType);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return this.entityManagerFactory.createEntityManager(synchronizationType, map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return this.entityManagerFactory.getCriteriaBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Metamodel getMetamodel() {
        return this.entityManagerFactory.getMetamodel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return this.entityManagerFactory.isOpen();
    }

    /**
     * Not allowing the consumer code to invoke the close on managed EntityManagerFactory.
     *
     * @throws UnsupportedOperationException if consumer code invokes the {@link EntityManagerFactory#close} method.
     */
    @Override
    public void close() {
        throw new UnsupportedOperationException("Managed EntityManagerFactory can't be closed by consumer code!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getProperties() {
        return this.entityManagerFactory.getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cache getCache() {
        return this.entityManagerFactory.getCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.entityManagerFactory.getPersistenceUnitUtil();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNamedQuery(String name, Query query) {
        this.entityManagerFactory.addNamedQuery(name, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> cls) {
        return this.entityManagerFactory.unwrap(cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        this.entityManagerFactory.addNamedEntityGraph(graphName, entityGraph);
    }
}
