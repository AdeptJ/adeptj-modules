package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.CrudDTO;
import com.adeptj.modules.data.jpa.DeleteCriteria;
import com.adeptj.modules.data.jpa.JpaSystemException;
import com.adeptj.modules.data.jpa.ReadCriteria;
import com.adeptj.modules.data.jpa.TupleQueryCriteria;
import com.adeptj.modules.data.jpa.UpdateCriteria;
import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
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
 * <p>
 * <code>
 * <p>
 * &#064;Reference(target="(osgi.unit.name=pu)")
 * private JpaCrudRepository repository;
 * <p>
 * </code>
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class EclipseLinkCrudRepository implements JpaCrudRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseLinkCrudRepository.class);

    private static final int LEN_ZERO = 0;

    private EntityManagerFactory emf;

    EclipseLinkCrudRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T insert(T entity) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            em.persist(entity);
            txn.commit();
            return entity;
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while inserting entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int updateByCriteria(UpdateCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<T> cu = cb.createCriteriaUpdate(criteria.getEntity());
            criteria.getUpdateAttributes().forEach(cu::set);
            int rowsUpdated = em.createQuery(cu.where(cb.and(this.predicates(criteria.getCriteriaAttributes(), cb,
                    cu.from(criteria.getEntity())).toArray(new Predicate[LEN_ZERO])))).executeUpdate();
            txn.commit();
            LOGGER.info("No. of rows updated: {}", rowsUpdated);
            return rowsUpdated;
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while updating by UpdateCriteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> void delete(Class<T> entity, Object primaryKey) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            T entityToDelete = em.find(entity, primaryKey);
            if (entityToDelete == null) {
                LOGGER.warn("Entity couldn't be deleted as it doesn't exists in DB: [{}]", entity);
            } else {
                txn.begin();
                em.remove(entityToDelete);
                txn.commit();
            }
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while deleting entity!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByJpaNamedQuery(CrudDTO<T> crudDTO) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            TypedQuery<T> typedQuery = em.createNamedQuery(crudDTO.getNamedQuery(), crudDTO.getEntity());
            this.setOrdinalParameters(typedQuery, crudDTO.getOrdinalParams());
            int rowsDeleted = typedQuery.executeUpdate();
            txn.commit();
            LOGGER.info("deleteByJpaNamedQuery: No. of rows deleted: {}", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            this.setRollbackOnly(txn);
            LOGGER.error("Exception while deleting by UpdateCriteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.rollbackTxn(txn);
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByCriteria(DeleteCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        try {
            txn.begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(criteria.getEntity());
            int rowsDeleted = em.createQuery(cd
                    .where(cb.and(this.predicates(criteria.getCriteriaAttributes(), cb, cd.from(criteria.getEntity()))
                            .toArray(new Predicate[LEN_ZERO])))).executeUpdate();
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T findById(Class<T> entity, Object primaryKey) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return em.find(entity, primaryKey);
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding by UpdateCriteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            return em.createQuery(cq.where(cb.and(this.predicates(criteria.getCriteriaAttributes(), cb,
                    cq.from(criteria.getEntity())).toArray(new Predicate[LEN_ZERO])))).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding by UpdateCriteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<Tuple> findByTupleQuery(TupleQueryCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.multiselect(criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .collect(Collectors.toList())
                    .toArray(new Selection[LEN_ZERO]))
                    .where(cb.and(this.predicates(criteria.getCriteriaAttributes(), cb, root)
                            .toArray(new Predicate[LEN_ZERO]))))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByCriteria with limiting results!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecordsByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            TypedQuery<T> typedQuery = em.createQuery(cq.where(cb.and(this.predicates(criteria.getCriteriaAttributes(),
                    cb, cq.from(criteria.getEntity())).toArray(new Predicate[LEN_ZERO]))));
            typedQuery.setFirstResult(criteria.getStartPos());
            typedQuery.setMaxResults(criteria.getMaxResult());
            return typedQuery.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByCriteria with limiting results!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByJpaNamedQuery(CrudDTO<T> crudDTO) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> query = em.createNamedQuery(crudDTO.getNamedQuery(), crudDTO.getEntity());
            this.setOrdinalParameters(query, crudDTO.getOrdinalParams());
            return query.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding entity by named query!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findByNamedQuery(String namedQuery, List<Object> ordinalParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Query query = em.createNamedQuery(namedQuery);
            this.setOrdinalParameters(query, ordinalParams);
            return (List<Object[]>) query.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while getting ScalarResult!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecords(Class<T> entity, int startPos, int maxResult) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            TypedQuery<T> typedQuery = em.createQuery(cq.select(cq.from(entity)));
            typedQuery.setFirstResult(startPos);
            typedQuery.setMaxResults(maxResult);
            return typedQuery.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while finding all Entities!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            this.setOrdinalParameters(query, crudDTO.getOrdinalParams());
            return query.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByJpaQuery!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecordsByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> typedQuery = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            this.setOrdinalParameters(typedQuery, crudDTO.getOrdinalParams());
            typedQuery.setFirstResult(crudDTO.getStartPos());
            typedQuery.setMaxResults(crudDTO.getMaxResult());
            return typedQuery.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByJpaQuery with limiting results!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            Root<T> root = cq.from(entity);
            return em.createQuery(cq
                    .select(root)
                    .where(root.get(attributeName).in(values)))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while findByINOperator!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAndMapResultSet(Class<T> resultClass, String nativeSql, String resultSetMapping, List<Object> ordinalParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Query query = em.createNativeQuery(nativeSql, resultSetMapping);
            this.setOrdinalParameters(query, ordinalParams);
            return (List<T>) query.getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while getting ScalarResult!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getScalarResultByNamedQuery(Class<T> resultClass, String namedQuery, List<Object> ordinalParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> typedQuery = em.createNamedQuery(namedQuery, resultClass);
            this.setOrdinalParameters(typedQuery, ordinalParams);
            return typedQuery.getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while getting ScalarResult!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> Long count(Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            return em.createQuery(cq.select(cb.count(cq.from(entity)))).getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while count query!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> Long countByCriteria(Class<T> entity, Map<String, Object> criteriaAttributes) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> from = cq.from(entity);
            return em.createQuery(cq
                    .select(cb.count(from))
                    .where(cb.and(this.predicates(criteriaAttributes, cb, from).toArray(new Predicate[LEN_ZERO]))))
                    .getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while countByCriteria!!", ex);
            throw new JpaSystemException(ex.getMessage(), ex);
        } finally {
            this.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager getEntityManager() {
        return this.emf.createEntityManager();
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

    private <T> List<Predicate> predicates(Map<String, Object> attributes, CriteriaBuilder cb, Root<T> root) {
        return attributes
                .entrySet()
                .stream()
                .map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * This method sets the positional query parameters passed by the caller.
     *
     * @param query         the JPA Query
     * @param ordinalParams Ordinal Parameters
     */
    private void setOrdinalParameters(Query query, List<Object> ordinalParams) {
        Objects.requireNonNull(ordinalParams, "Ordinal Parameters cannot be null!!");
        AtomicInteger ordinalCounter = new AtomicInteger();
        ordinalParams.forEach(param -> query.setParameter(ordinalCounter.incrementAndGet(), param));
    }
}
