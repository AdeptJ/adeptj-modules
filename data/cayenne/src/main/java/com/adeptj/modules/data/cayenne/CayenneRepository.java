package com.adeptj.modules.data.cayenne;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.exp.Expression;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@ConsumerType
public interface CayenneRepository<T> {

    T insert(T entity);

    T insert(Class<T> entity, Consumer<T> consumer);

    T updateById(Object id, Class<T> entity, Consumer<T> consumer);

    T updateByExpression(Class<T> entity, Expression expression, Consumer<T> consumer);

    T findById(Object id, Class<T> entity);

    T findOneByExpression(Class<T> entity, Expression expression);

    List<T> findManyByExpression(Class<T> entity, Expression expression);

    List<T> findAll(Class<T> entity);

    void deleteById(Object id, Class<T> entity);

    <E> E doInCayenne(@NotNull Function<ServerRuntime, E> function);

    void doInCayenne(@NotNull Consumer<ServerRuntime> consumer);
}
