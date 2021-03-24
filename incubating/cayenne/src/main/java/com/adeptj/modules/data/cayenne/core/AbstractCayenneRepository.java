package com.adeptj.modules.data.cayenne.core;

import com.adeptj.modules.data.cayenne.CayenneRepository;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.exp.Expression;
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

    protected ObjectContext newContext() {
        return this.cayenne.newContext();
    }

    @Override
    public T insert(T entity) {
        ObjectContext context = this.newContext();
        context.registerNewObject(entity);
        context.commitChanges();
        return entity;
    }

    @Override
    public T insert(Class<T> entity, Consumer<T> consumer) {
        ObjectContext context = this.newContext();
        T object = context.newObject(entity);
        consumer.accept(object);
        context.commitChanges();
        return object;
    }

    @Override
    public T updateById(Object id, Class<T> entity, Consumer<T> consumer) {
        ObjectContext context = this.newContext();
        T one = SelectById.query(entity, id).selectOne(context);
        if (one == null) {
            throw new IllegalStateException(String.format("No record found for entity (%s) with id (%s)",
                    entity.getName(), id));
        }
        consumer.accept(one);
        context.commitChanges();
        return one;
    }

    @Override
    public T updateByExpression(Class<T> entity, Expression expression, Consumer<T> consumer) {
        ObjectContext context = this.newContext();
        T one = ObjectSelect.query(entity, expression).selectOne(context);
        if (one == null) {
            throw new IllegalStateException(String.format("No record found for entity (%s) with expression (%s)",
                    entity.getName(), expression));
        }
        consumer.accept(one);
        context.commitChanges();
        return one;
    }

    @Override
    public T findById(Object id, Class<T> entity) {
        return SelectById.query(entity, id).selectOne(this.newContext());
    }

    @Override
    public T findOneByExpression(Class<T> entity, Expression expression) {
        return ObjectSelect.query(entity, expression).selectOne(this.newContext());
    }

    @Override
    public List<T> findManyByExpression(Class<T> entity, Expression expression) {
        return ObjectSelect.query(entity, expression).select(this.newContext());
    }

    @Override
    public List<T> findAll(Class<T> entity) {
        return ObjectSelect.query(entity).select(this.newContext());
    }

    @Override
    public void deleteById(Object id, Class<T> entity) {
        ObjectContext context = this.newContext();
        T one = SelectById.query(entity, id).selectOne(context);
        if (one == null) {
            throw new IllegalStateException(String.format("No record found for entity (%s) with id (%s)",
                    entity.getName(), id));
        }
        context.deleteObject(one);
        context.commitChanges();
    }

    @Override
    public <E> E doInCayenne(@NotNull Function<ServerRuntime, E> function) {
        return function.apply(this.getCayenne());
    }

    @Override
    public void doInCayenne(@NotNull Consumer<ServerRuntime> consumer) {
        consumer.accept(this.getCayenne());
    }
}
