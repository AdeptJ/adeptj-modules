package com.adeptj.modules.data.cayenne.core;

import com.adeptj.modules.data.cayenne.CayenneRepository;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@ConsumerType
public abstract class AbstractCayenneRepository<T> implements CayenneRepository<T> {

    private volatile ServerRuntime cayenne;

    protected ServerRuntime getCayenne() {
        Validate.isTrue((this.cayenne != null), "Cayenne ServerRuntime is null!!");
        return cayenne;
    }

    public void setCayenne(ServerRuntime cayenne) {
        this.cayenne = cayenne;
    }

    @Override
    public <E> E doInCayenne(@NotNull Function<ServerRuntime, E> function) {
        return function.apply(this.getCayenne());
    }

    @Override
    public void doInCayenne(@NotNull Consumer<ServerRuntime> consumer) {
        consumer.accept(this.getCayenne());
    }

    @Override
    public T findById(Object id, Class<T> entity) {
        return SelectById.query(entity, id).selectOne(this.cayenne.newContext());
    }

    @Override
    public List<T> findMany(Class<T> entity) {
        return ObjectSelect.query(entity).select(this.cayenne.newContext());
    }
}
