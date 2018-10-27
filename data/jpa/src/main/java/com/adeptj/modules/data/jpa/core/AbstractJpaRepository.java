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
import com.adeptj.modules.data.jpa.JpaUtil;
import com.adeptj.modules.data.jpa.Predicates;
import com.adeptj.modules.data.jpa.QueryType;
import com.adeptj.modules.data.jpa.Transactions;
import com.adeptj.modules.data.jpa.criteria.ConstructorCriteria;
import com.adeptj.modules.data.jpa.criteria.DeleteCriteria;
import com.adeptj.modules.data.jpa.criteria.ReadCriteria;
import com.adeptj.modules.data.jpa.criteria.TupleQueryCriteria;
import com.adeptj.modules.data.jpa.criteria.UpdateCriteria;
import com.adeptj.modules.data.jpa.dto.CrudDTO;
import com.adeptj.modules.data.jpa.dto.ResultSetMappingDTO;
import com.adeptj.modules.data.jpa.exception.JpaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Implementation of {@link JpaRepository} based on EclipseLink JPA Reference Implementation
 * <p>
 * This will be registered with the OSGi service registry whenever there is a new EntityManagerFactory configuration
 * created from OSGi console.
 * <p>
 * Therefore there will be a separate service for each PersistenceUnit.
 * <p>
 * Callers will have to provide an OSGi filter while injecting a reference of {@link JpaRepository}
 *
 * <code>
 * &#064;Reference(target="(osgi.unit.name=my_persistence_unit)")
 * private JpaRepository repository;
 * </code>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public abstract class AbstractJpaRepository implements JpaRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * The {@link EntityManagerFactory} instance is a proxy and the lifecycle of actual instance is managed by
     * the {@link com.adeptj.modules.data.jpa.internal.JpaRepositoryFactory} therefore consumers must not
     * attempt to create or close it on their own. It will be automatically created and closed appropriately.
     */
    protected EntityManagerFactory entityManagerFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T insert(T entity) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
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
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T update(T entity) {
        T updated;
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            em.getTransaction().begin();
            updated = em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.close(em);
        }
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int updateByCriteria(UpdateCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<T> cu = cb.createCriteriaUpdate(criteria.getEntity());
            criteria.getUpdateAttributes().forEach(cu::set);
            Root<T> root = cu.from(criteria.getEntity());
            int rowsUpdated = em
                    .createQuery(cu.where(cb.and(Predicates.from(criteria.getCriteriaAttributes(), cb, root))))
                    .executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("No. of rows updated: {}", rowsUpdated);
            return rowsUpdated;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> void delete(Class<T> entity, Object primaryKey) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            em.getTransaction().begin();
            T entityToDelete = em.find(entity, primaryKey);
            if (entityToDelete == null) {
                LOGGER.warn("Entity couldn't be deleted as it doesn't exists in DB: [{}]", entity);
            } else {
                em.remove(entityToDelete);
                em.getTransaction().commit();
            }
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByJpaNamedQuery(CrudDTO<T> crudDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            em.getTransaction().begin();
            TypedQuery<T> typedQuery = em.createNamedQuery(crudDTO.getNamedQuery(), crudDTO.getEntity());
            JpaUtil.setQueryParams(typedQuery, crudDTO.getPosParams());
            int rowsDeleted = typedQuery.executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("deleteByJpaNamedQuery: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByCriteria(DeleteCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(criteria.getEntity());
            Root<T> root = cd.from(criteria.getEntity());
            int rowsDeleted = em
                    .createQuery(cd.where(cb.and(Predicates.from(criteria.getCriteriaAttributes(), cb, root))))
                    .executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("deleteByCriteria: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteAll(Class<T> entity) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
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
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T findById(Class<T> entity, Object primaryKey) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            return em.find(entity, primaryKey);
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.where(cb.and(Predicates.from(criteria.getCriteriaAttributes(), cb, root))))
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<Tuple> findByTupleQuery(TupleQueryCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.multiselect(criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .toArray(Selection[]::new))
                    .where(cb.and(Predicates.from(criteria.getCriteriaAttributes(), cb, root))))
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecordsByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq
                    .where(cb.and(Predicates.from(criteria.getCriteriaAttributes(), cb, root))))
                    .setFirstResult(criteria.getStartPos())
                    .setMaxResults(criteria.getMaxResult())
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findByJpaNamedQuery(Class<T> resultClass, String namedQuery, List<Object> posParams) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            TypedQuery<T> query = em.createNamedQuery(namedQuery, resultClass);
            JpaUtil.setQueryParams(query, posParams);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByNamedQuery(String namedQuery, List<Object> posParams) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            Query query = em.createNamedQuery(namedQuery);
            JpaUtil.setQueryParams(query, posParams);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> entity) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            return em.createQuery(cq.select(cq.from(entity))).getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecords(Class<T> entity, int startPos, int maxResult) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            return em.createQuery(cq.select(cq.from(entity)))
                    .setFirstResult(startPos)
                    .setMaxResults(maxResult)
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            TypedQuery<T> query = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            JpaUtil.setQueryParams(query, crudDTO.getPosParams());
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecordsByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            TypedQuery<T> typedQuery = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            JpaUtil.setQueryParams(typedQuery, crudDTO.getPosParams());
            return typedQuery.setFirstResult(crudDTO.getStartPos())
                    .setMaxResults(crudDTO.getMaxResult())
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            Root<T> root = cq.from(entity);
            return em.createQuery(cq
                    .select(root)
                    .where(root.get(attributeName).in(values)))
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByQueryAndMapDefault(Class<T> resultClass, String nativeQuery, List<Object> posParams) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            Query query = em.createNativeQuery(nativeQuery, resultClass);
            JpaUtil.setQueryParams(query, posParams);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByQueryAndMapResultSet(Class<T> resultClass, ResultSetMappingDTO mappingDTO) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            Query query = em.createNativeQuery(mappingDTO.getNativeQuery(), mappingDTO.getResultSetMapping());
            JpaUtil.setQueryParams(query, mappingDTO.getPosParams());
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findByQueryAndMapConstructor(Class<T> resultClass, String jpaQuery, List<Object> posParams) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            TypedQuery<T> query = em.createQuery(jpaQuery, resultClass);
            JpaUtil.setQueryParams(query, posParams);
            return query.getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity, C> List<C> findByCriteriaAndMapConstructor(ConstructorCriteria<T, C> criteria) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<C> cq = cb.createQuery(criteria.getConstructorClass());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.select(cb.construct(criteria.getConstructorClass(), criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .toArray(Selection[]::new)))
                    .where(Predicates.from(criteria.getCriteriaAttributes(), cb, root)))
                    .getResultList();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getScalarResultOfType(Class<T> resultClass, QueryType type, String query, List<Object> posParams) {
        T result = null;
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            switch (type) {
                case JPA:
                    TypedQuery<T> typedQuery = em.createQuery(query, resultClass);
                    JpaUtil.setQueryParams(typedQuery, posParams);
                    result = typedQuery.getSingleResult();
                    break;
                case NATIVE:
                    Query nativeQuery = em.createNativeQuery(query, resultClass);
                    JpaUtil.setQueryParams(nativeQuery, posParams);
                    result = resultClass.cast(nativeQuery.getSingleResult());
                    break;
            }
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getScalarResultOfType(Class<T> resultClass, String namedQuery, List<Object> posParams) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            TypedQuery<T> query = em.createNamedQuery(namedQuery, resultClass);
            JpaUtil.setQueryParams(query, posParams);
            return query.getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    @Override
    public Object getScalarResult(String namedQuery, List<Object> posParams) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            Query query = em.createNamedQuery(namedQuery);
            JpaUtil.setQueryParams(query, posParams);
            return query.getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    @Override
    public <T extends BaseEntity> Long count(Class<T> entity) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            return em.createQuery(cq.select(cb.count(cq.from(entity)))).getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    @Override
    public Number count(String query, QueryType type) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            switch (type) {
                case JPA:
                    return (Number) em.createQuery(query).getSingleResult();
                case NATIVE:
                    return (Number) em.createNativeQuery(query).getSingleResult();
                default:
                    throw new IllegalStateException("Invalid QueryType!!");
            }
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    @Override
    public Number count(String namedQuery) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            return (Number) em.createNamedQuery(namedQuery).getSingleResult();
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(JpaCallback<T> action) {
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            return action.doInJpa(em);
        } catch (Exception ex) { // NOSONAR
            throw new JpaException(ex);
        } finally {
            JpaUtil.close(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T executeInTransaction(JpaCallback<T> action) {
        T result;
        EntityManager em = JpaUtil.createEntityManager(this.entityManagerFactory);
        try {
            em.getTransaction().begin();
            result = action.doInJpa(em);
            em.getTransaction().commit();
        } catch (Exception ex) { // NOSONAR
            Transactions.markRollback(em);
            throw new JpaException(ex);
        } finally {
            Transactions.rollback(em);
            JpaUtil.close(em);
        }
        return result;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
}
