package com.adeptj.modules.data.cayenne;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@ConsumerType
public interface CayenneRepository<T> {

    <E> E doInCayenne(@NotNull Function<ServerRuntime, E> function);

    void doInCayenne(@NotNull Consumer<ServerRuntime> consumer);

    T findById(Object id, Class<T> entity);

    List<T> findMany(Class<T> entity);
}
