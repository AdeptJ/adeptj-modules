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
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.Query;
import jakarta.persistence.SchemaManager;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A simple wrapper for {@link EntityManagerFactory}, this way EntityManagerFactory object isn't directly
 * exposed which prevents the possibility of closing a managed EntityManagerFactory by consumer.
 * exposed, which prevents the possibility of closing a managed EntityManagerFactory by consumer.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class EntityManagerFactoryWrapper implements EntityManagerFactory {

    private final EntityManagerFactory delegate;

    EntityManagerFactoryWrapper(@NotNull EntityManagerFactory delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager createEntityManager() {
        return this.delegate.createEntityManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager createEntityManager(Map map) {
        return this.delegate.createEntityManager(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return this.delegate.createEntityManager(synchronizationType);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return this.delegate.createEntityManager(synchronizationType, map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return this.delegate.getCriteriaBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Metamodel getMetamodel() {
        return this.delegate.getMetamodel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return this.delegate.isOpen();
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

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getProperties() {
        return this.delegate.getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cache getCache() {
        return this.delegate.getCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.delegate.getPersistenceUnitUtil();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return this.delegate.getTransactionType();
    }

    @Override
    public SchemaManager getSchemaManager() {
        return this.delegate.getSchemaManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNamedQuery(String name, Query query) {
        this.delegate.addNamedQuery(name, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> cls) {
        return this.delegate.unwrap(cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        this.delegate.addNamedEntityGraph(graphName, entityGraph);
    }

    @Override
    public <R> Map<String, TypedQueryReference<R>> getNamedQueries(Class<R> resultType) {
        return this.delegate.getNamedQueries(resultType);
    }

    @Override
    public <E> Map<String, EntityGraph<? extends E>> getNamedEntityGraphs(Class<E> entityType) {
        return this.delegate.getNamedEntityGraphs(entityType);
    }

    @Override
    public void runInTransaction(Consumer<EntityManager> work) {
        this.delegate.runInTransaction(work);
    }

    @Override
    public <R> R callInTransaction(Function<EntityManager, R> work) {
        return this.delegate.callInTransaction(work);
    }
}