package com.adeptj.modules.data.mongodb.api;

import com.mongodb.Function;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.Validate;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.mongojack.JacksonMongoCollection;
import org.mongojack.MongoCollection;
import org.osgi.annotation.versioning.ConsumerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ConsumerType
public abstract class AbstractMongoRepository<T> implements MongoRepository<T> {

    private volatile JacksonMongoCollection<T> mongoCollection;

    private volatile MongoClient mongoClient;

    private final Class<T> documentClass;

    /**
     * Initializes the {@link #documentClass} field from the type parameter specified by the subclass.
     * <p>
     * Following rules apply:
     * <p>
     * 1. If the subclass is not parameterized then an exception is thrown.
     * 2. If the subclass is parameterized and the type parameter class is annotated with @MongoCollection
     */
    @SuppressWarnings("unchecked")
    public AbstractMongoRepository() {
        Type type = this.getClass().getGenericSuperclass();
        // 1. check if the subclass is parameterized.
        if (type instanceof ParameterizedType parameterizedType) {
            this.documentClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
            // 2. subclass is parameterized, now check if the type parameter class is annotated with @MongoCollection.
            MongoCollection annotation = this.documentClass.getAnnotation(MongoCollection.class);
            if (annotation == null) {
                throw new IllegalStateException("Class " + this.documentClass + " is not annotated with @MongoCollection!!");
            }
        } else {
            throw new IllegalStateException("Class " + type.getTypeName() + " is not parameterized, please provide"
                    + " the type parameter of the class annotated with @MongoCollection!!");
        }
    }

    protected JacksonMongoCollection<T> getMongoCollection() {
        Validate.validState((this.mongoCollection != null), "JacksonMongoCollection is null!");
        return this.mongoCollection;
    }

    public void setMongoCollection(JacksonMongoCollection<T> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    public MongoClient getMongoClient() {
        Validate.validState((this.mongoClient != null), "MongoClient is null!");
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Class<T> getDocumentClass() {
        return this.documentClass;
    }

    /**
     * Gets the Logger of the subclass.
     * <p>
     * Note: subclasses can override this method and return a cached(declared as static) {@link Logger} instance.
     *
     * @return the {@link Logger}
     */
    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    // << --------------------------- Public --------------------------- >>

    @Override
    public void insert(T document) {
        this.getMongoCollection().insert(document);
    }

    @Override
    public void insertMany(List<T> documents) {
        this.getMongoCollection().insert(documents);
    }

    @Override
    public UpdateResult insertOrUpdate(T document) {
        return this.getMongoCollection().save(document);
    }

    @Override
    public T findOneById(Object id) {
        return this.getMongoCollection().findOneById(id);
    }

    @Override
    public List<T> findMany() {
        return this.getMongoCollection().find().into(new ArrayList<>());
    }

    @Override
    public List<T> findMany(Bson filter) {
        return this.getMongoCollection().find(filter).into(new ArrayList<>());
    }

    @Override
    public UpdateResult updateById(Object id, Bson bson) {
        return this.getMongoCollection().updateById(id, bson);
    }

    @Override
    public UpdateResult replaceOneById(Object id, T document) {
        return this.getMongoCollection().replaceOneById(id, document);
    }

    @Override
    public DeleteResult removeById(Object id) {
        return this.getMongoCollection().removeById(id);
    }

    @Override
    public T doWithMongoClient(@NotNull Function<MongoClient, T> function) {
        return function.apply(this.getMongoClient());
    }

    @Override
    public void doWithMongoClient(@NotNull Consumer<MongoClient> consumer) {
        consumer.accept(this.getMongoClient());
    }
}
