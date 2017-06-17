/*
 * =============================================================================
 *
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * =============================================================================
 */
package com.adeptj.modules.data.jpa.impl;

import com.adeptj.modules.data.jpa.EntityManagerProvider;
import com.adeptj.modules.data.jpa.JPAPersistenceService;
import com.adeptj.modules.data.jpa.JpaPersistenceException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Persistence service for CRUD operations on DB using Eclipselink JPA Provider.
 *
 * @author princearora
 */
@Component
public class JPAPersistenceServiceImpl implements JPAPersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAPersistenceServiceImpl.class);

    @Reference
    private EntityManagerProvider emProvider;

    @Override
    public <T> T insert(T transientObj) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.emProvider.getEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            entityManager.persist(transientObj);
            txn.commit();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while inserting entity!!", ex);
            this.rollbackTxn(txn);
            throw new JpaPersistenceException(ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
        return transientObj;
    }

    @Override
    public <T> T update(T persistentObj) {
        T updated = null;
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.emProvider.getEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            updated = entityManager.merge(persistentObj);
            txn.commit();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while updating entity!!", ex);
            this.rollbackTxn(txn);
            throw new JpaPersistenceException(ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
        return updated;
    }

    @Override
    public <T> List<T> findByCriteria(Class<T> criteriaClass, Map<String, Object> queryParams) {
        EntityManager entityManager = null;
        try {
            entityManager = this.emProvider.getEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteriaClass);
            List<Predicate> predicates = this.toPredicates(queryParams, cb, cq.from(criteriaClass));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<T> typedQuery = entityManager.createQuery(cq);
            return typedQuery.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while finding entity!!", ex);
            throw new JpaPersistenceException(ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T> List<T> findByNamedQuery(Class<T> criteriaClass, String namedQuery, Object... queryParams) {
        EntityManager entityManager = null;
        try {
            entityManager = this.emProvider.getEntityManager();
            TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, criteriaClass);
            this.setQueryParameters(query, queryParams);
            return query.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while finding entity by named query!!", ex);
            throw new JpaPersistenceException(ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        EntityManager entityManager = null;
        try {
            entityManager = this.emProvider.getEntityManager();
            CriteriaQuery<T> cq = entityManager.getCriteriaBuilder().createQuery(entityClass);
            return entityManager.createQuery(cq.select(cq.from(entityClass))).getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while finding entities!!", ex);
            throw new JpaPersistenceException(ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T> void delete(T entityInstance) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.emProvider.getEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            entityManager.remove(entityManager.contains(entityInstance) ? entityInstance : entityManager.merge(entityInstance));
            txn.commit();
        } catch (PersistenceException | EclipseLinkException | IllegalArgumentException ex) {
            LOGGER.error("Exception while deleting entity!!", ex);
            this.rollbackTxn(txn);
            throw new JpaPersistenceException(ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T> int deleteByCriteria(Class<T> entity, Map<String, Object> predicateMap) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.emProvider.getEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaDelete<T> delete = cb.createCriteriaDelete(entity);
            List<Predicate> predicates = this.toPredicates(predicateMap, cb, delete.from(entity));
            delete.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            int rowCount = entityManager.createQuery(delete).executeUpdate();
            LOGGER.info("deleteByCriteria: No. of rows deleted: {}", rowCount);
            txn.commit();
            return rowCount;
        } catch (PersistenceException | EclipseLinkException | IllegalArgumentException ex) {
            LOGGER.error("Exception while deleting entity by criteria!!", ex);
            this.rollbackTxn(txn);
            throw new JpaPersistenceException(ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    private void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    private void rollbackTxn(EntityTransaction txn) {
        if (txn != null && txn.isActive()) {
            txn.rollback();
        }
    }

    private <T> List<Predicate> toPredicates(Map<String, Object> predicateMap, CriteriaBuilder cb, Root<T> root) {
        return predicateMap.entrySet().stream().map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * This method sets the positional query parameters passed by the caller.
     *
     * @param query the JPA Query
     * @param queryParams Named Parameters
     */
    private void setQueryParameters(TypedQuery<?> query, Object... queryParams) {
        if (queryParams != null && queryParams.length > 0) {
            // Positional parameters always starts with 1.
            for (int i = 0; i < queryParams.length; i++) {
                query.setParameter(i + 1, queryParams[i]);
            }
        }
    }
}
