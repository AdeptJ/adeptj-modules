package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.JpaSystemException;
import com.adeptj.modules.data.jpa.api.BaseEntity;
import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JpaCrudRepository} based on EclipseLink JPA Reference Implementation
 * <p>
 * This will be registered with the OSGi service registry whenever there is a new EntityManagerFactory configuration
 * saved by {@link EntityManagerFactoryProvider}
 * <p>
 * Therefore there will be a separate service for each PersistenceUnit.
 * <p>
 * Callers will have to provide an OSGi filter while injecting a reference of {@link JpaCrudRepository}
 *
 * <code>
 *
 *     &#064;Reference(target="(osgi.unit.name=pu)")
 *     private JpaCrudRepository repository;
 *
 * </code>
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class EclipseLinkCrudRepository implements JpaCrudRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseLinkCrudRepository.class);

    private EntityManagerFactory emf;

    EclipseLinkCrudRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public <T extends BaseEntity> void insert(T entity) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            em.persist(entity);
            txn.commit();
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while inserting entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> T update(T entity) {
        T updated;
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            updated = em.merge(entity);
            txn.commit();
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while updating entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
        return updated;
    }

    @Override
    public <T extends BaseEntity> int updateByCriteria(Class<T> entity, Map<String, Object> namedParams, Map<String, Object> updateFields) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<T> cu = cb.createCriteriaUpdate(entity);
            updateFields.forEach(cu::set);
            cu.where(cb.and(this.predicates(namedParams, cb, cu.from(entity)).toArray(new Predicate[0])));
            int rowsUpdated = em.createQuery(cu).executeUpdate();
            txn.commit();
            LOGGER.info("No. of rows updated: {}", rowsUpdated);
            return rowsUpdated;
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while updating by Criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> void delete(T entity) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            em.remove(em.merge(entity));
            txn.commit();
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while deleting entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> int deleteByNamedQuery(Class<T> entity, String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            TypedQuery<T> typedQuery = em.createNamedQuery(namedQuery, entity);
            this.setQueryParameters(typedQuery, posParams);
            int rowsDeleted = typedQuery.executeUpdate();
            txn.commit();
            LOGGER.info("deleteByNamedQuery: No. of rows deleted: {}", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while deleting by Criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> int deleteByCriteria(Class<T> entity, Map<String, Object> namedParams) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(entity);
            cd.where(cb.and(this.predicates(namedParams, cb, cd.from(entity)).toArray(new Predicate[0])));
            int rowsDeleted = em.createQuery(cd).executeUpdate();
            txn.commit();
            LOGGER.info("deleteByCriteria: No. of rows deleted: {}", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while deleting entity by criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> int deleteAll(Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            int rowsDeleted = em.createQuery(em.getCriteriaBuilder().createCriteriaDelete(entity)).executeUpdate();
            txn.commit();
            LOGGER.info("deleteAll: No. of rows deleted: {}", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while deleting all Entities!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> T findById(Class<T> entity, Object primaryKey) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return em.find(entity, primaryKey);
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding by Criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByCriteria(Class<T> entity, Map<String, Object> namedParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entity);
            cq.where(cb.and(this.predicates(namedParams, cb, cq.from(entity)).toArray(new Predicate[0])));
            return em.createQuery(cq).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding by Criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByCriteria(Class<T> entity, Map<String, Object> namedParams, int startPos, int maxResult) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entity);
            cq.where(cb.and(this.predicates(namedParams, cb, cq.from(entity)).toArray(new Predicate[0])));
            TypedQuery<T> typedQuery = em.createQuery(cq);
            typedQuery.setFirstResult(startPos);
            typedQuery.setMaxResults(maxResult);
            return typedQuery.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByCriteria with limiting results!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByNamedQuery(Class<T> entity, String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> query = em.createNamedQuery(namedQuery, entity);
            this.setQueryParameters(query, posParams);
            return query.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding entity by named query!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            return em.createQuery(cq.select(cq.from(entity))).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding entities!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> entity, int startPos, int maxResult) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            cq.select(cq.from(entity));
            TypedQuery<T> typedQuery = em.createQuery(cq);
            typedQuery.setMaxResults(maxResult);
            typedQuery.setFirstResult(startPos);
            return typedQuery.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding all Entities!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByQuery(String jpaQuery, Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return em.createQuery(jpaQuery, entity).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByQuery!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByQuery(String jpaQuery, Class<T> entity, int startPos, int maxResult) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> typedQuery = em.createQuery(jpaQuery, entity);
            typedQuery.setFirstResult(startPos);
            typedQuery.setMaxResults(maxResult);
            return typedQuery.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByQuery with limiting results!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByINOperator(Map<String, List<Object>> inParams, Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        try {
            String entityAttr = inParams.keySet().iterator().next();
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            Root<T> root = cq.from(entity);
            cq.select(root).where(root.get(entityAttr).in(inParams.entrySet().iterator().next().getValue()));
            return em.createQuery(cq).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByINOperator!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <E> E getScalarResultByNamedQuery(Class<E> entity, String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<E> typedQuery = em.createNamedQuery(namedQuery, entity);
            this.setQueryParameters(typedQuery, posParams);
            return typedQuery.getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while getting ScalarResult!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> Long count(Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(entity)));
            return em.createQuery(cq).getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while count query!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    @Override
    public <T extends BaseEntity> Long countByCriteria(Class<T> entity, Map<String, Object> namedParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> from = cq.from(entity);
            cq.select(cb.count(from));
            cq.where(cb.and(this.predicates(namedParams, cb, from).toArray(new Predicate[0])));
            return em.createQuery(cq).getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while countByCriteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    private void closeEntityManager(EntityManager em) {
        try {
            if (em.isOpen()) {
                em.close();
            }
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while closing EntityManager!!", ex);
        }
    }

    private void setRollbackOnly(EntityTransaction txn) {
        if (txn != null && !txn.getRollbackOnly()) {
            txn.setRollbackOnly();
        }
    }

    private void rollbackTxn(EntityTransaction txn) {
        try {
            if (txn != null && txn.isActive() && txn.getRollbackOnly()) {
                LOGGER.warn("Rolling back transaction!!");
                txn.rollback();
            }
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while rolling back transaction!!", ex);
        }
    }

    private <T> List<Predicate> predicates(Map<String, Object> namedParams, CriteriaBuilder cb, Root<T> root) {
        return namedParams.entrySet().stream().map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * This method sets the positional query parameters passed by the caller.
     *
     * @param query       the JPA Query
     * @param posParams Positional Parameters
     */
    private void setQueryParameters(TypedQuery<?> query, List<Object> posParams) {
        Objects.requireNonNull(posParams, "Positional Parameters cannot be null!!");
        for (int i = 0; i < posParams.size(); i++) {
            // Positional parameters always starts with 1.
            query.setParameter(i + 1, posParams.get(i));
        }
    }
}
