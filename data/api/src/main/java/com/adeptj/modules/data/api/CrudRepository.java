package com.adeptj.modules.data.api;

import java.util.Optional;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public interface CrudRepository {

    <T> void save(T entity);

    <T> void saveAll(Iterable<T> entities);

    <T> Optional<T> findById(Object id);

    boolean existsById(Object id);

    <T> Iterable<T> findAll();

    <T> Iterable<T> findAllByIds(Iterable<Object> ids);

    long count();

    void deleteById(Object id);

    <T> void delete(T entity);

    <T> void deleteAll(Iterable<? extends T> entities);

    void deleteAll();
}
