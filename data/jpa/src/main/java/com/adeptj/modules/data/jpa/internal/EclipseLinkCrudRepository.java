package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.JpaSystemException;
import com.adeptj.modules.data.jpa.api.BaseEntity;
import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
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

    private EntityManagerFactory entityManagerFactory;

    EclipseLinkCrudRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public <T extends BaseEntity> void insert(T entity) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            entityManager.persist(entity);
            txn.commit();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            this.rollbackTxn(txn);
            LOGGER.error("Exception while inserting entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> T update(T entity) {
        T updated;
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            updated = entityManager.merge(entity);
            txn.commit();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            this.rollbackTxn(txn);
            LOGGER.error("Exception while updating entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
        return updated;
    }

    @Override
    public <T extends BaseEntity> int updateByCriteria(Class<T> entityClass, Map<String, Object> namedParams, Map<String, Object> updateFields) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<T> update = cb.createCriteriaUpdate(entityClass);
            updateFields.forEach((param, value) ->  update.set(param, value));
            List<Predicate> predicates = this.toPredicates(namedParams, cb, update.from(entityClass));
            update.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            int rowCount = entityManager.createQuery(update).executeUpdate();
            txn.commit();
            LOGGER.info("No. of rows updated: {}", rowCount);
            return rowCount;
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            this.rollbackTxn(txn);
            LOGGER.error("Exception while updating by Criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> void delete(T entity) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            txn.commit();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            this.rollbackTxn(txn);
            LOGGER.error("Exception while deleting entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> int deleteByNamedQuery(Class<T> entityClass, String namedQuery, List<Object> positionalParams) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            TypedQuery<T> typedQuery = entityManager.createNamedQuery(namedQuery, entityClass);
            this.setQueryParameters(typedQuery, positionalParams);
            int rowsDeleted = typedQuery.executeUpdate();
            txn.commit();
            LOGGER.info("deleteByNamedQuery: No. of rows deleted: {}", rowsDeleted);
            return rowsDeleted;
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            this.rollbackTxn(txn);
            LOGGER.error("Exception while deleting by Criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> int deleteByCriteria(Class<T> entityClass, Map<String, Object> namedParams) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClass);
            List<Predicate> predicates = this.toPredicates(namedParams, cb, delete.from(entityClass));
            delete.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            int rowCount = entityManager.createQuery(delete).executeUpdate();
            txn.commit();
            LOGGER.info("deleteByCriteria: No. of rows deleted: {}", rowCount);
            return rowCount;
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            this.rollbackTxn(txn);
            LOGGER.error("Exception while deleting entity by criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> int deleteAll(Class<T> entityClass) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            int rowCount = entityManager.createQuery(entityManager.getCriteriaBuilder().createCriteriaDelete(entityClass))
                    .executeUpdate();
            txn.commit();
            LOGGER.info("deleteAll: No. of rows deleted: {}", rowCount);
            return rowCount;
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            this.rollbackTxn(txn);
            LOGGER.error("Exception while deleting all Entities!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByCriteria(Class<T> criteriaClass, Map<String, Object> namedParams) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteriaClass);
            List<Predicate> predicates = this.toPredicates(namedParams, cb, cq.from(criteriaClass));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            return entityManager.createQuery(cq).getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while finding by Criteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByCriteria(Class<T> criteriaClass, Map<String, Object> namedParams, int startIndex, int limit) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteriaClass);
            List<Predicate> predicates = this.toPredicates(namedParams, cb, cq.from(criteriaClass));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<T> typedQuery = entityManager.createQuery(cq);
            typedQuery.setFirstResult(startIndex);
            typedQuery.setMaxResults(limit);
            return typedQuery.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while findByCriteria with limiting results!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByNamedQuery(Class<T> criteriaClass, String namedQuery, List<Object> positionalParams) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, criteriaClass);
            this.setQueryParameters(query, positionalParams);
            return query.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while finding entity by named query!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> entityClass) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            CriteriaQuery<T> cq = entityManager.getCriteriaBuilder().createQuery(entityClass);
            return entityManager.createQuery(cq.select(cq.from(entityClass))).getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while finding entities!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findByCriteriaWithINParams(Map<String, List<Object>> inParams, Class<T> criteriaClass) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            String entityAttr = inParams.keySet().iterator().next();
            CriteriaQuery<T> cq = entityManager.getCriteriaBuilder().createQuery(criteriaClass);
            Root<T> root = cq.from(criteriaClass);
            cq.select(root).where(root.get(entityAttr).in(inParams.entrySet().iterator().next().getValue()));
            return entityManager.createQuery(cq).getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while findByCriteriaWithINParams!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> entityClass, int startIndex, int limit) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            CriteriaQuery<T> cq = entityManager.getCriteriaBuilder().createQuery(entityClass);
            cq.select(cq.from(entityClass));
            TypedQuery<T> typedQuery = entityManager.createQuery(cq);
            typedQuery.setMaxResults(limit);
            typedQuery.setFirstResult(startIndex);
            return typedQuery.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while finding all Entities!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <E> E getScalarResultByNamedQuery(Class<E> scalarResultClass, String namedQuery, List<Object> positionalParams) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            TypedQuery<E> typedQuery = entityManager.createNamedQuery(namedQuery, scalarResultClass);
            this.setQueryParameters(typedQuery, positionalParams);
            return typedQuery.getSingleResult();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while getting ScalarResult!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> Long count(Class<T> entityClass) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(entityClass)));
            return entityManager.createQuery(cq).getSingleResult();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while count query!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> Long countByCriteria(Class<T> entityClass, Map<String, Object> namedParams) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> from = cq.from(entityClass);
            cq.select(cb.count(from));
            List<Predicate> predicates = this.toPredicates(namedParams, cb, from);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            return entityManager.createQuery(cq).getSingleResult();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while countByCriteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> selectByQuery(String jpaQuery, Class<T> entityClass) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            return entityManager.createQuery(jpaQuery, entityClass).getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while selectByQuery!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(entityManager);
        }
    }

    @Override
    public <T extends BaseEntity> List<T> selectByQuery(String jpaQuery, Class<T> entityClass, int startIndex, int limit) {
        EntityManager entityManager = null;
        try {
            entityManager = this.entityManagerFactory.createEntityManager();
            TypedQuery<T> typedQuery = entityManager.createQuery(jpaQuery, entityClass);
            typedQuery.setFirstResult(startIndex);
            typedQuery.setMaxResults(limit);
            return typedQuery.getResultList();
        } catch (PersistenceException | EclipseLinkException | IllegalStateException | IllegalArgumentException ex) {
            LOGGER.error("Exception while selectByQuery with limiting results!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
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

    private <T> List<Predicate> toPredicates(Map<String, Object> namedParams, CriteriaBuilder cb, Root<T> root) {
        return namedParams.entrySet().stream().map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * This method sets the positional query parameters passed by the caller.
     *
     * @param query       the JPA Query
     * @param positionalParams Positional Parameters
     */
    private void setQueryParameters(TypedQuery<?> query, List<Object> positionalParams) {
        Objects.requireNonNull(positionalParams, "Positional Parameters cannot be null!!");
        // Positional parameters always starts with 1.
        for (int i = 0; i < positionalParams.size(); i++) {
            query.setParameter(i + 1, positionalParams.get(i));
        }
    }
}
