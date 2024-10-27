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
import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.criteria.BaseCriteria;
import com.adeptj.modules.data.jpa.criteria.ConstructorCriteria;
import com.adeptj.modules.data.jpa.criteria.DeleteCriteria;
import com.adeptj.modules.data.jpa.criteria.ReadCriteria;
import com.adeptj.modules.data.jpa.criteria.TupleCriteria;
import com.adeptj.modules.data.jpa.criteria.UpdateCriteria;
import com.adeptj.modules.data.jpa.exception.JpaException;
import com.adeptj.modules.data.jpa.query.InParam;
import com.adeptj.modules.data.jpa.query.OutParam;
import com.adeptj.modules.data.jpa.query.QueryParam;
import com.adeptj.modules.data.jpa.util.JpaUtil;
import com.adeptj.modules.data.jpa.util.Predicates;
import com.adeptj.modules.data.jpa.util.Transactions;
import com.adeptj.modules.data.jpa.wrapper.EntityManagerWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jakarta.persistence.ParameterMode.OUT;

/**
 * Abstract implementation of {@link JpaRepository}.
 * <p>
 * The consumer should subclass this and register with the OSGi service registry.
 * <p>
 * The EntityManagerFactoryLifecycle will bind to the {@link JpaRepository} implementations as and when they
 * become available and set an active {@link EntityManagerFactory} instance to the {@link AbstractJpaRepository}.
 *
 * @param <T>  The {@link BaseEntity} subclass type which this repository is dealing with.
 * @param <ID> The primary key of the JPA entity.
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public abstract class AbstractJpaRepository<T extends BaseEntity, ID extends Serializable> implements JpaRepository<T, ID> {

    /**
     * The {@link EntityManagerFactory}'s lifecycle is managed by EntityManagerFactoryLifecycle therefore consumers
     * must not attempt to create or close it on their own.
     * <p>
     * It will be created and closed appropriately.
     */
    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    protected EntityManagerFactory getEntityManagerFactory() {
        if (this.entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory is null!!");
        }
        return this.entityManagerFactory;
    }

    /**
     * Gets the Logger of the subclass.
     * <p>
     * Note: subclasses can override this method as return a cached(declared as static) instance.
     *
     * @return the {@link Logger}
     */
    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected void beginTransaction(@NotNull EntityManager em) {
        em.getTransaction().begin();
    }

    protected void commitTransaction(@NotNull EntityManager em) {
        em.getTransaction().commit();
    }

    protected EntityManager getEntityManager() {
        return this.getEntityManagerFactory().createEntityManager();
    }

    // <---------------------------------------------- Insert ---------------------------------------------->>

    /**
     * {@inheritDoc}
     */
    @Override
    public T insert(T entity) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            this.beginTransaction(em);
            em.persist(entity);
            this.commitTransaction(em);
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
        Validate.isTrue((entities != null && !entities.isEmpty()), "Entity list is null or empty!!");
        Validate.isTrue(batchSize > 1, "batchSize should be greater than 1!!");
        EntityManager em = this.getEntityManager();
        try {
            this.beginTransaction(em);
            for (int i = 0; i < entities.size(); i++) {
                T entity = entities.get(i);
                if (entity != null) {
                    if (i > 0 && (i % batchSize == 0)) {
                        this.commitTransaction(em);
                        em.clear();
                        this.beginTransaction(em);
                    }
                    em.persist(entity);
                }
            }
            this.commitTransaction(em);
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    // <---------------------------------------------- Update ---------------------------------------------->>

    /**
     * {@inheritDoc}
     */
    @Override
    public T update(T entity) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        Object id = this.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        if (id == null) {
            throw new IllegalStateException("Entity is not a detached entity, not merging it.");
        }
        T updated;
        EntityManager em = this.getEntityManager();
        try {
            this.beginTransaction(em);
            updated = em.merge(entity);
            this.commitTransaction(em);
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
        int rowsUpdated = this.executeCriteriaQuery(criteria);
        this.getLogger().debug("No. of rows updated: {}", rowsUpdated);
        return rowsUpdated;
    }

    // <---------------------------------------------- Delete ---------------------------------------------->>

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Class<T> entity, ID primaryKey) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            T entityToDelete = em.find(entity, primaryKey);
            if (entityToDelete == null) {
                this.getLogger().warn("Entity couldn't be deleted because it doesn't exists in DB: [{}]", entity);
            } else {
                this.beginTransaction(em);
                em.remove(entityToDelete);
                this.commitTransaction(em);
            }
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
    public int deleteByJpaNamedQuery(String name, QueryParam... params) {
        EntityManager em = this.getEntityManager();
        try {
            Query query = em.createNamedQuery(name);
            JpaUtil.bindQueryParams(query, params);
            this.beginTransaction(em);
            int rowsDeleted = query.executeUpdate();
            this.commitTransaction(em);
            this.getLogger().debug("deleteByJpaNamedQuery: No. of rows deleted: [{}]", rowsDeleted);
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
        int rowsDeleted = this.executeCriteriaQuery(criteria);
        this.getLogger().debug("deleteByCriteria: No. of rows deleted: [{}]", rowsDeleted);
        return rowsDeleted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteAll(Class<T> entity) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(entity);
            Query query = em.createQuery(cd);
            this.beginTransaction(em);
            int rowsDeleted = query.executeUpdate();
            this.commitTransaction(em);
            this.getLogger().debug("deleteAll: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    private int executeCriteriaQuery(BaseCriteria<T> criteria) {
        EntityManager em = this.getEntityManager();
        try {
            Query criteriaQuery = this.getCriteriaQuery(em, criteria);
            if (criteriaQuery == null) { // This should never happen.
                this.getLogger().warn("Could not create criteria query!!");
                return 0;
            }
            this.beginTransaction(em);
            int rowsAffected = criteriaQuery.executeUpdate();
            this.commitTransaction(em);
            return rowsAffected;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
    }

    private Query getCriteriaQuery(EntityManager em, BaseCriteria<T> criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Query query = null;
        if (criteria instanceof UpdateCriteria<T> updateCriteria) {
            CriteriaUpdate<T> cu = cb.createCriteriaUpdate(updateCriteria.getEntity());
            updateCriteria.getUpdateAttributes().forEach(cu::set);
            Root<T> root = cu.from(updateCriteria.getEntity());
            cu.where(Predicates.from(cb, root, updateCriteria));
            query = em.createQuery(cu);
        } else if (criteria instanceof DeleteCriteria) {
            CriteriaDelete<T> cd = cb.createCriteriaDelete(criteria.getEntity());
            Root<T> root = cd.from(criteria.getEntity());
            cd.where(Predicates.from(cb, root, criteria));
            query = em.createQuery(cd);
        }
        return query;
    }

    // <---------------------------------------------- Find ---------------------------------------------->>

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(Class<T> entity, ID primaryKey) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
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
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            Root<T> root = cq.from(criteria.getEntity());
            cq.where(Predicates.from(cb, root, criteria));
            return em.createQuery(cq).getResultList();
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
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<T> root = cq.from(criteria.getEntity());
            cb.tuple(this.getTupleSelections(criteria, root));
            cq.where(Predicates.from(cb, root, criteria));
            return em.createQuery(cq).getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @NotNull
    private List<Selection<?>> getTupleSelections(@NotNull TupleCriteria<T> criteria, Root<T> root) {
        return criteria.getSelections()
                .stream()
                .map(selection -> {
                    Path<?> path = root.get(selection.getAttributeName());
                    if (StringUtils.isNotEmpty(selection.getAlias())) {
                        path.alias(selection.getAlias());
                    }
                    return path;
                })
                .collect(Collectors.toList());
    }

    @Override
    public <E> List<E> findAttributeValuesByCriteria(Class<T> entity, String attributeName, Class<E> attributeType) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<E> cq = cb.createQuery(attributeType);
            Root<T> root = cq.from(entity);
            cq.select(root.get(attributeName));
            return em.createQuery(cq).getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Object[]> findMultiAttributeValuesByCriteria(Class<T> entity, String... attributeNames) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<T> root = cq.from(entity);
            Path<?>[] selections = Stream.of(attributeNames)
                    .map(root::get)
                    .toArray(Path[]::new);
            cq.select(cb.array(selections));
            return em.createQuery(cq).getResultList();
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
    public <E> List<E> findByNamedQuery(Class<E> resultClass, String name, QueryParam... params) {
        Validate.isTrue(resultClass != null, "resultClass argument can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            TypedQuery<E> query = em.createNamedQuery(name, resultClass);
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
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            Root<T> root = cq.from(entity);
            cq.select(root);
            return em.createQuery(cq).getResultList();
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
    public List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values, boolean negation) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entity);
            Root<T> root = cq.from(entity);
            cq.select(root);
            Predicate restriction = root.get(attributeName).in(values);
            if (negation) {
                cq.where(cb.not(restriction));
            } else {
                cq.where(restriction);
            }
            return em.createQuery(cq).getResultList();
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
    public <C> List<C> findByCriteriaWithDTOProjection(ConstructorCriteria<T, C> criteria) {
        Validate.isTrue(criteria != null, "ConstructorCriteria can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<C> cq = cb.createQuery(criteria.getConstructorClass());
            Root<T> root = cq.from(criteria.getEntity());
            Selection<?>[] selections = criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .toArray(Selection[]::new);
            cq.select(cb.construct(criteria.getConstructorClass(), selections));
            cq.where(Predicates.from(cb, root, criteria));
            return em.createQuery(cq).getResultList();
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
    public <E> E findSingleResultByNamedQuery(Class<E> resultClass, String name, QueryParam... params) {
        EntityManager em = this.getEntityManager();
        try {
            TypedQuery<E> query = em.createNamedQuery(name, resultClass);
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
    public Long countByCriteria(Class<T> entity) {
        Validate.isTrue(entity != null, "Entity can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(entity);
            Expression<Long> expression = cb.count(root);
            cq.select(expression);
            return em.createQuery(cq).getSingleResult();
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
    public Long countByNamedQuery(String name) {
        Validate.isTrue(StringUtils.isNotEmpty(name), "NamedQuery string can't be null!");
        EntityManager em = this.getEntityManager();
        try {
            return (Long) em.createNamedQuery(name).getSingleResult();
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
    public <E> E doWithEntityManager(Function<EntityManager, E> function, boolean requiresTxn) {
        Validate.isTrue(function != null, "Function can't be null!");
        E result;
        EntityManager em = this.getEntityManager();
        try {
            if (requiresTxn) {
                this.beginTransaction(em);
                result = function.apply(new EntityManagerWrapper(em));
                this.commitTransaction(em);
            } else {
                result = function.apply(new EntityManagerWrapper(em));
            }
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.closeEntityManager(em);
        }
        return result;
    }

    // <---------------------------------------- Stored Procedures ---------------------------------------->>

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeNamedStoredProcedure(String name, List<InParam> params, String outParamName) {
        EntityManager em = this.getEntityManager();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeStoredProcedure(String procedureName, List<InParam> params, OutParam outParam) {
        EntityManager em = this.getEntityManager();
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> findByNamedStoredProcedure(String name, InParam... params) {
        EntityManager em = this.getEntityManager();
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> findByStoredProcedure(Class<E> resultClass, String procedureName, InParam... params) {
        EntityManager em = this.getEntityManager();
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
