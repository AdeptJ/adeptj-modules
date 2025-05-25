package com.adeptj.modules.data.jpa.wrapper;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FindOption;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.Query;
import jakarta.persistence.RefreshOption;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;
import java.util.Map;

public class EntityManagerWrapper implements EntityManager {

    private final EntityManager delegate;

    public EntityManagerWrapper(EntityManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public void persist(Object entity) {
        this.delegate.persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        return this.delegate.merge(entity);
    }

    @Override
    public void remove(Object entity) {
        this.delegate.remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return this.delegate.find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return this.delegate.find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return this.delegate.find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return this.delegate.find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, FindOption... options) {
        return this.delegate.find(entityClass, primaryKey, options);
    }

    @Override
    public <T> T find(EntityGraph<T> entityGraph, Object primaryKey, FindOption... options) {
        return this.delegate.find(entityGraph, primaryKey, options);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return this.delegate.getReference(entityClass, primaryKey);
    }

    @Override
    public <T> T getReference(T entity) {
        return this.delegate.getReference(entity);
    }

    @Override
    public void flush() {
        this.delegate.flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        this.delegate.setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return this.delegate.getFlushMode();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        this.delegate.lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        this.delegate.lock(entity, lockMode, properties);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, LockOption... options) {
        this.delegate.lock(entity, lockMode, options);
    }

    @Override
    public void refresh(Object entity) {
        this.delegate.refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        this.delegate.refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        this.delegate.refresh(entity, lockMode);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        this.delegate.refresh(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity, RefreshOption... options) {
        this.delegate.refresh(entity, options);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public void detach(Object entity) {
        this.delegate.detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return this.delegate.contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return this.delegate.getLockMode(entity);
    }

    @Override
    public void setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        this.delegate.setCacheRetrieveMode(cacheRetrieveMode);
    }

    @Override
    public void setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        this.delegate.setCacheStoreMode(cacheStoreMode);
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        return this.delegate.getCacheRetrieveMode();
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        return this.delegate.getCacheStoreMode();
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        this.delegate.setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.delegate.getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        return this.delegate.createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return this.delegate.createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaSelect<T> selectQuery) {
        return this.delegate.createQuery(selectQuery);
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        return this.delegate.createQuery(updateQuery);
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        return this.delegate.createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return this.delegate.createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        return this.delegate.createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return this.delegate.createNamedQuery(name, resultClass);
    }

    @Override
    public <T> TypedQuery<T> createQuery(TypedQueryReference<T> reference) {
        return this.delegate.createQuery(reference);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return this.delegate.createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        return this.delegate.createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return this.delegate.createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return this.delegate.createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return this.delegate.createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        return this.delegate.createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        return this.delegate.createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        this.delegate.joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return this.delegate.isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return this.delegate.unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return this.delegate.getDelegate();
    }

    @Override
    public void close() {
        // NOOP - not allowing the caller to close the EntityManager.
    }

    @Override
    public boolean isOpen() {
        return this.delegate.isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return this.delegate.getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        // Not allowing the caller to close the underlying EntityManagerFactory.
        return null;
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return this.delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return this.delegate.getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return this.delegate.createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return this.delegate.createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return this.delegate.getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return this.delegate.getEntityGraphs(entityClass);
    }

    @Override
    public <C> void runWithConnection(ConnectionConsumer<C> action) {
        this.delegate.runWithConnection(action);
    }

    @Override
    public <C, T> T callWithConnection(ConnectionFunction<C, T> function) {
        return this.delegate.callWithConnection(function);
    }
}