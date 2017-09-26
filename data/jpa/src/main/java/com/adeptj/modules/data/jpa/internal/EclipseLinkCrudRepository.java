package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.ConstructorCriteria;
import com.adeptj.modules.data.jpa.CrudDTO;
import com.adeptj.modules.data.jpa.DeleteCriteria;
import com.adeptj.modules.data.jpa.JpaUtil;
import com.adeptj.modules.data.jpa.PersistenceException;
import com.adeptj.modules.data.jpa.ReadCriteria;
import com.adeptj.modules.data.jpa.ResultSetMappingDTO;
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
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.adeptj.modules.data.jpa.JpaUtil.LEN_ZERO;

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

    private EntityManagerFactory emf;

    public EclipseLinkCrudRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T insert(T entity) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            em.persist(entity);
            txn.commit();
            return entity;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T update(T entity) {
        T updated;
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            updated = em.merge(entity);
            txn.commit();
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int updateByCriteria(UpdateCriteria<T> criteria) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<T> cu = cb.createCriteriaUpdate(criteria.getEntity());
            criteria.getUpdateAttributes().forEach(cu::set);
            Root<T> root = cu.from(criteria.getEntity());
            int rowsUpdated = em
                    .createQuery(cu.where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .executeUpdate();
            txn.commit();
            LOGGER.debug("No. of rows updated: {}", rowsUpdated);
            return rowsUpdated;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> void delete(Class<T> entity, Object primaryKey) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            T entityToDelete = em.find(entity, primaryKey);
            if (entityToDelete == null) {
                LOGGER.warn("Entity couldn't be deleted as it doesn't exists in DB: [{}]", entity);
            } else {
                em.remove(entityToDelete);
                txn.commit();
            }
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByJpaNamedQuery(CrudDTO<T> crudDTO) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            TypedQuery<T> typedQuery = em.createNamedQuery(crudDTO.getNamedQuery(), crudDTO.getEntity());
            int rowsDeleted = JpaUtil.typedQueryWithParams(typedQuery, crudDTO.getPosParams())
                    .executeUpdate();
            txn.commit();
            LOGGER.debug("deleteByJpaNamedQuery: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByCriteria(DeleteCriteria<T> criteria) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(criteria.getEntity());
            Root<T> root = cd.from(criteria.getEntity());
            int rowsDeleted = em
                    .createQuery(cd.where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .executeUpdate();
            txn.commit();
            LOGGER.debug("deleteByCriteria: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteAll(Class<T> entity) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            int rowsDeleted = em
                    .createQuery(em.getCriteriaBuilder().createCriteriaDelete(entity))
                    .executeUpdate();
            txn.commit();
            LOGGER.debug("deleteAll: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
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
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq
                    .where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
                    .where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq
                    .where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .setFirstResult(criteria.getStartPos())
                    .setMaxResults(criteria.getMaxResult())
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findByJpaNamedQuery(Class<T> resultClass, String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.typedQueryWithParams(em.createNamedQuery(namedQuery, resultClass), posParams)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByNamedQuery(String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.queryWithParams(em.createNamedQuery(namedQuery), posParams)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            return em.createQuery(cq.select(cq.from(entity)))
                    .setFirstResult(startPos)
                    .setMaxResults(maxResult)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            return JpaUtil.typedQueryWithParams(query, crudDTO.getPosParams())
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            return JpaUtil.typedQueryWithParams(typedQuery, crudDTO.getPosParams())
                    .setFirstResult(crudDTO.getStartPos())
                    .setMaxResults(crudDTO.getMaxResult())
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAndMapResultSet(Class<T> resultClass, ResultSetMappingDTO mappingDTO) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Query query = em.createNativeQuery(mappingDTO.getNativeQuery(), mappingDTO.getResultSetMapping());
            return JpaUtil.queryWithParams(query, mappingDTO.getPosParams())
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findAndMapConstructorByQuery(Class<T> resultClass, String jpaQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.typedQueryWithParams(em.createQuery(jpaQuery, resultClass), posParams)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity, C> List<C> findAndMapConstructorByCriteria(ConstructorCriteria<T, C> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<C> cq = cb.createQuery(criteria.getConstructorClass());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.select(cb.construct(criteria.getConstructorClass(), criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .collect(Collectors.toList())
                    .toArray(new Selection[LEN_ZERO])))
                    .where(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root)))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getScalarResultOfType(Class<T> resultClass, String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.typedQueryWithParams(em.createNamedQuery(namedQuery, resultClass), posParams)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @Override
    public Object getScalarResult(String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.queryWithParams(em.createNamedQuery(namedQuery), posParams)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
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
                    .where(cb.and(JpaUtil.getPredicates(criteriaAttributes, cb, from))))
                    .getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager getEntityManager() {
        return this.emf.createEntityManager();
    }
}
