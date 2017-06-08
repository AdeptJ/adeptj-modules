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
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author princearora
 */
@Component(name = "AdeptJ JPA Persistence Service", immediate = true)
public class JPAPersistenceServiceImpl implements JPAPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(JPAPersistenceServiceImpl.class);

    @Reference
    private EntityManagerProvider managerProvider;

    private static EntityManager entityManager;

    @Activate
    protected void activate(ComponentContext context) {
        try {
            this.entityManager = this.managerProvider.getEntityManager();
        } catch (SQLException ex) {
            log.error("Unable to create Entity manager: ", ex);
        }
    }

    @Deactivate
    protected void deactivate() {
        this.entityManager = null;
    }


    @Override
    public <T> T insert(T transientObj) {
        try {
            this.entityManager.persist(transientObj);
            this.entityManager.getTransaction().commit();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            throw new JpaPersistenceException(ex);
        }
        return transientObj;
    }

    @Override
    public <T> T update(T persistentObj) {
        T updated = null;
        try {
            updated = this.entityManager.merge(persistentObj);
            this.entityManager.getTransaction().commit();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            throw new JpaPersistenceException(ex);
        }
        return updated;
    }

    @Override
    public <T> List<T> findByCriteria(Class<T> criteriaClass, Map<String, Object> queryParams)
                throws JpaPersistenceException {
        try {
            CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteriaClass);
            List<Predicate> predicates = this.getPredicates(queryParams, cb, cq.from(criteriaClass));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<T> typedQuery = this.entityManager.createQuery(cq);
            return typedQuery.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            throw new JpaPersistenceException(ex);
        }
    }

    @Override
    public <T> List<T> findByNamedQuery(Class<T> criteriaClass, String namedQuery, Object... queryParams)
                throws JpaPersistenceException {
        try {
            TypedQuery<T> tquery = this.entityManager.createNamedQuery(namedQuery, criteriaClass);
            this.setQueryParameters(tquery, queryParams);
            return tquery.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            throw new JpaPersistenceException(ex);
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) throws JpaPersistenceException {
        try {
            CriteriaQuery<T> cq = this.entityManager.getCriteriaBuilder().createQuery(entityClass);
            return this.entityManager.createQuery(cq.select(cq.from(entityClass))).getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            throw new JpaPersistenceException(ex);
        }
    }

    /**
     * @param predicateMap
     * @param cb
     * @param root
     * @param <T>
     * @return
     */
    private <T> List<Predicate> getPredicates(Map<String, Object> predicateMap, CriteriaBuilder cb,
                                                                 Root<T> root) {
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, ?> entry : predicateMap.entrySet()) {
            predicates.add(cb.equal(root.get(entry.getKey()), entry.getValue()));
        }
        return predicates;
    }

    /**
     * This method sets the positional query parameters passed by the caller.
     *
     * @param tquery
     * @param queryParams
     */
    private void setQueryParameters(TypedQuery<?> tquery, Object... queryParams) {
        if (queryParams != null && queryParams.length > 0) {
            for (int i = 0; i < queryParams.length; i++) {
                Object queryParam = queryParams[i];
                // Positional parameters always starts with 1.
                tquery.setParameter(i + 1, queryParam);
            }
        }
    }
}
