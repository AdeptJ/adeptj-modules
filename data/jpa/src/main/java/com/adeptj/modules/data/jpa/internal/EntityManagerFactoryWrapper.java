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

import org.apache.commons.lang3.Validate;

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

    private static final String EMF_NULL_EXCEPTION_MSG = "EntityManagerFactory is null, probably due to missing persistence.xml!!";

    private final EntityManagerFactory delegate;

    EntityManagerFactoryWrapper(EntityManagerFactory delegate) {
        Validate.validState(delegate != null, EMF_NULL_EXCEPTION_MSG);
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
     */
    @Override
    public void close() {
        throw new IllegalStateException("Managed EntityManagerFactory can't be closed by consumer code!!");
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
}
