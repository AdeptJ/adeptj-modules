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

package com.adeptj.modules.data.jpa.core;

import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.JpaCallback;
import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.criteria.ConstructorCriteria;
import com.adeptj.modules.data.jpa.criteria.DeleteCriteria;
import com.adeptj.modules.data.jpa.criteria.ReadCriteria;
import com.adeptj.modules.data.jpa.criteria.TupleCriteria;
import com.adeptj.modules.data.jpa.criteria.UpdateCriteria;
import com.adeptj.modules.data.jpa.dto.CrudDTO;
import com.adeptj.modules.data.jpa.dto.ResultSetMappingDTO;
import com.adeptj.modules.data.jpa.exception.JpaException;
import com.adeptj.modules.data.jpa.internal.EntityManagerFactoryLifecycle;
import com.adeptj.modules.data.jpa.query.InParam;
import com.adeptj.modules.data.jpa.query.OutParam;
import com.adeptj.modules.data.jpa.query.QueryParam;
import com.adeptj.modules.data.jpa.query.QueryType;
import com.adeptj.modules.data.jpa.util.JpaUtil;
import com.adeptj.modules.data.jpa.util.Predicates;
import com.adeptj.modules.data.jpa.util.Transactions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.annotation.versioning.ConsumerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;

import static javax.persistence.ParameterMode.OUT;

/**
 * Abstract implementation of {@link JpaRepository} based on EclipseLink JPA Reference Implementation.
 * <p>
 * The consumer should subclass this and registered with the OSGi service registry.
 * <p>
 * The {@link EntityManagerFactoryLifecycle} will bind to the {@link JpaRepository} implementations as and when they
 * become available and set an active {@link EntityManagerFactory} instance to the {@link AbstractJpaRepository}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public abstract class AbstractJpaRepository<T extends BaseEntity, ID extends Serializable> implements JpaRepository<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * The {@link EntityManagerFactory}'s lifecycle is managed by {@link EntityManagerFactoryLifecycle}
     * therefore consumers must not attempt to create or close it on their own.
     * <p>
     * It will be automatically created and closed appropriately.
     */
    private EntityManagerFactory entityManagerFactory;

    protected EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T insert(T entity) {
        Validate.notNull(entity, "Entity can't be null!");
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    @Override
    public void batchInsert(List<T> entities, int batchSize) {
        Validate.noNullElements(entities);
        Validate.isTrue(batchSize > 1, "batchSize should be greater than 1!!");
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            em.getTransaction().begin();
            for (int i = 0; i < entities.size(); i++) {
                if (i > 0 && (i % batchSize == 0)) {
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                    em.clear();
                }
                em.persist(entities.get(i));
            }
            em.getTransaction().commit();
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T update(T entity) {
        Validate.notNull(entity, "Entity can't be null!");
        T updated;
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            em.getTransaction().begin();
            updated = em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateByCriteria(UpdateCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<T> cu = cb.createCriteriaUpdate(criteria.getEntity());
            criteria.getUpdateAttributes().forEach(cu::set);
            Root<T> root = cu.from(criteria.getEntity());
            Predicate[] predicates = Predicates.using(cb, root, criteria.getCriteriaAttributes());
            em.getTransaction().begin();
            int rowsUpdated = em.createQuery(cu.where(predicates)).executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("No. of rows updated: {}", rowsUpdated);
            return rowsUpdated;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Class<T> entity, ID primaryKey) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            // This should happen in a single transaction so start the transaction right away.
            em.getTransaction().begin();
            T entityToDelete = em.find(entity, primaryKey);
            if (entityToDelete == null) {
                LOGGER.warn("Entity couldn't be deleted as it doesn't exists in DB: [{}]", entity);
            } else {
                em.remove(entityToDelete);
            }
            em.getTransaction().commit();
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteByJpaNamedQuery(CrudDTO<T> crudDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            em.getTransaction().begin();
            TypedQuery<T> query = em.createNamedQuery(crudDTO.getNamedQueryName(), crudDTO.getEntity());
            JpaUtil.bindQueryParams(query, crudDTO.getQueryParams());
            int rowsDeleted = query.executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("deleteByJpaNamedQuery: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteByCriteria(DeleteCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(criteria.getEntity());
            Root<T> root = cd.from(criteria.getEntity());
            Predicate[] predicates = Predicates.using(cb, root, criteria.getCriteriaAttributes());
            int rowsDeleted = em.createQuery(cd.where(predicates)).executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("deleteByCriteria: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteAll(Class<T> entity) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            em.getTransaction().begin();
            int rowsDeleted = em.createQuery(em.getCriteriaBuilder().createCriteriaDelete(entity)).executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("deleteAll: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(Class<T> entity, ID primaryKey) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            return em.find(entity, primaryKey);
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            Root<T> root = cq.from(criteria.getEntity());
            Predicate[] predicates = Predicates.using(cb, root, criteria.getCriteriaAttributes());
            return em.createQuery(cq.where(predicates)).getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tuple> findByTupleCriteria(TupleCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.multiselect(criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .toArray(Selection[]::new))
                    .where(Predicates.using(cb, root, criteria.getCriteriaAttributes())))
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findPaginatedRecordsByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq
                    .where(Predicates.using(cb, root, criteria.getCriteriaAttributes())))
                    .setFirstResult(criteria.getStartPos())
                    .setMaxResults(criteria.getMaxResult())
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> List<E> findByJpaNamedQuery(Class<E> resultClass, String namedQuery, QueryParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            TypedQuery<E> query = em.createNamedQuery(namedQuery, resultClass);
            JpaUtil.bindQueryParams(query, params);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> findByNamedQuery(String namedQuery, QueryParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            Query query = em.createNamedQuery(namedQuery);
            JpaUtil.bindQueryParams(query, params);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(Class<T> entity) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            return em.createQuery(cq.select(cq.from(entity))).getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findPaginatedRecords(Class<T> entity, int startPos, int maxResult) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            return em.createQuery(cq.select(cq.from(entity)))
                    .setFirstResult(startPos)
                    .setMaxResults(maxResult)
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            TypedQuery<T> query = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            JpaUtil.bindQueryParams(query, crudDTO.getQueryParams());
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findPaginatedRecordsByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            TypedQuery<T> query = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            JpaUtil.bindQueryParams(query, crudDTO.getQueryParams());
            return query.setFirstResult(crudDTO.getStartPos())
                    .setMaxResults(crudDTO.getMaxResult())
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            Root<T> root = cq.from(entity);
            Predicate predicate = root.get(attributeName).in(values);
            return em.createQuery(cq.select(root).where(predicate)).getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> findByQueryAndMapDefault(Class<E> resultClass, String nativeQuery, QueryParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            Query query = em.createNativeQuery(nativeQuery, resultClass);
            JpaUtil.bindQueryParams(query, params);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> findByQueryAndMapResultSet(Class<E> resultClass, ResultSetMappingDTO mappingDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            Query query = em.createNativeQuery(mappingDTO.getNativeQuery(), mappingDTO.getResultSetMapping());
            JpaUtil.bindQueryParams(query, mappingDTO.getQueryParams());
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> List<E> findByQueryAndMapConstructor(Class<E> resultClass, String jpaQuery, QueryParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            TypedQuery<E> query = em.createQuery(jpaQuery, resultClass);
            JpaUtil.bindQueryParams(query, params);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> List<C> findByCriteriaAndMapConstructor(ConstructorCriteria<T, C> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<C> cq = cb.createQuery(criteria.getConstructorClass());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.select(cb.construct(criteria.getConstructorClass(), criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .toArray(Selection[]::new)))
                    .where(Predicates.using(cb, root, criteria.getCriteriaAttributes())))
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> E getScalarResultOfType(Class<E> resultClass, QueryType type, String query, QueryParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            switch (type) {
                case JPA:
                    TypedQuery<E> typedQuery = em.createQuery(query, resultClass);
                    JpaUtil.bindQueryParams(typedQuery, params);
                    return typedQuery.getSingleResult();
                case NATIVE:
                    Query nativeQuery = em.createNativeQuery(query, resultClass);
                    JpaUtil.bindQueryParams(nativeQuery, params);
                    return resultClass.cast(nativeQuery.getSingleResult());
                default:
                    throw new IllegalStateException("Invalid QueryType!!");
            }
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> E getScalarResultOfType(Class<E> resultClass, String namedQuery, QueryParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            TypedQuery<E> query = em.createNamedQuery(namedQuery, resultClass);
            JpaUtil.bindQueryParams(query, params);
            return query.getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getScalarResult(String namedQuery, QueryParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            Query query = em.createNamedQuery(namedQuery);
            JpaUtil.bindQueryParams(query, params);
            return query.getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long count(Class<T> entity) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            return em.createQuery(cq.select(cb.count(cq.from(entity)))).getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long count(String query, QueryType type) {
        Validate.isTrue(StringUtils.isNotEmpty(query), "Query string can't be null!");
        Validate.isTrue(type != null, "QueryType can't be null!");
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            switch (type) {
                case JPA:
                    return (Long) em.createQuery(query).getSingleResult();
                case NATIVE:
                    return (Long) em.createNativeQuery(query).getSingleResult();
            }
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
        return 0L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long count(String namedQueryName) {
        Validate.isTrue(StringUtils.isNotEmpty(namedQueryName), "NamedQuery string can't be null!");
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            return (Long) em.createNamedQuery(namedQueryName).getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> E executeCallback(JpaCallback<E> action) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            return action.doInJpa(em);
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> E executeCallbackInTransaction(JpaCallback<E> action) {
        E result;
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            em.getTransaction().begin();
            result = action.doInJpa(em);
            em.getTransaction().commit();
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
        return result;
    }

    @Override
    public Object executeNamedStoredProcedure(String name, List<InParam> params, String outParamName) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery(name);
            JpaUtil.bindNamedStoredProcedureInParams(query, params.toArray(new InParam[0]));
            query.execute();
            return query.getOutputParameterValue(outParamName);
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @Override
    public Object executeStoredProcedure(String procedureName, List<InParam> params, OutParam outParam) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery(procedureName);
            JpaUtil.bindStoredProcedureInParams(query, params.toArray(new InParam[0]));
            query.registerStoredProcedureParameter(outParam.getName(), outParam.getType(), OUT);
            query.execute();
            return query.getOutputParameterValue(outParam.getName());
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> findByNamedStoredProcedure(String name, InParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery(name);
            JpaUtil.bindNamedStoredProcedureInParams(query, params);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> findByStoredProcedure(Class<E> resultClass, String procedureName, InParam... params) {
        EntityManager em = JpaUtil.createEntityManager(this.getEntityManagerFactory());
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery(procedureName, resultClass);
            JpaUtil.bindStoredProcedureInParams(query, params);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }
}
