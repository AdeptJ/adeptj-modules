package com.adeptj.modules.data.mongodb.api;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.Validate;
import org.bson.conversions.Bson;
import org.mongojack.JacksonMongoCollection;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.ArrayList;
import java.util.List;

@ConsumerType
public abstract class AbstractMongoRepository<T> implements MongoRepository<T> {

    private volatile JacksonMongoCollection<T> mongoCollection;

    private final Class<T> documentClass;

    /**
     * Initializes the {@link #documentClass} field with a non null document class object.
     *
     * @param documentClass the document class required by {@link JacksonMongoCollection}
     */
    protected AbstractMongoRepository(Class<T> documentClass) {
        Validate.isTrue((documentClass != null), "documentClass must not be null!");
        this.documentClass = documentClass;
    }

    protected JacksonMongoCollection<T> getMongoCollection() {
        Validate.validState((this.mongoCollection != null), "JacksonMongoCollection is null!");
        return this.mongoCollection;
    }

    public void setMongoCollection(JacksonMongoCollection<T> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    public Class<T> getDocumentClass() {
        return this.documentClass;
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
}
