package com.adeptj.modules.data.mongodb.core;

import com.adeptj.modules.data.mongodb.MongoRepository;
import org.apache.commons.lang3.Validate;
import org.bson.conversions.Bson;
import org.mongojack.JacksonMongoCollection;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.ArrayList;
import java.util.List;

@ConsumerType
public abstract class AbstractMongoRepository<T> implements MongoRepository<T> {

    private JacksonMongoCollection<T> mongoCollection;

    private final Class<T> documentClass;

    /**
     * Initializes the {@link #documentClass} field with a non null document class object.
     *
     * @param documentClass the document class required by {@link JacksonMongoCollection}
     */
    protected AbstractMongoRepository(Class<T> documentClass) {
        Validate.isTrue(documentClass != null, "documentClass must not be null!");
        this.documentClass = documentClass;
    }

    protected JacksonMongoCollection<T> getMongoCollection() {
        Validate.validState(this.mongoCollection != null, "JacksonMongoCollection is null!");
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
    public void insert(T object) {
        this.getMongoCollection().insert(object);
    }

    @Override
    public T findOneById(Object id) {
        return this.getMongoCollection().findOneById(id);
    }

    @Override
    public List<T> findAll(Class<T> type) {
        List<T> documents = new ArrayList<>();
        this.getMongoCollection().find(type).forEach(documents::add);
        return documents;
    }

    @Override
    public void updateById(Object id, Bson bson) {
        this.getMongoCollection().updateById(id, bson);
    }
}
