package com.adeptj.modules.data.mongodb.core;

import com.adeptj.modules.data.mongodb.BaseDocument;
import com.adeptj.modules.data.mongodb.MongoRepository;
import org.apache.commons.lang3.Validate;
import org.bson.conversions.Bson;
import org.mongojack.JacksonMongoCollection;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.ArrayList;
import java.util.List;

@ConsumerType
public abstract class AbstractMongoRepository<T extends BaseDocument> implements MongoRepository<T> {

    /**
     * Kept protected so that subclasses could access it directly.
     */
    protected JacksonMongoCollection<T> mongoCollection;

    private final Class<T> documentClass;

    // << --------------------------- Internal --------------------------- >>

    /**
     * Initializes the {@link #documentClass} field with a non null document class object.
     *
     * @param documentClass the document class required by {@link JacksonMongoCollection}
     */
    protected AbstractMongoRepository(Class<T> documentClass) {
        Validate.isTrue(documentClass != null, "DocumentClass must not be null!");
        this.documentClass = documentClass;
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
        this.mongoCollection.insert(object);
    }

    @Override
    public T findOneById(Object id) {
        return this.mongoCollection.findOneById(id);
    }

    @Override
    public List<T> findAll(Class<T> type) {
        List<T> documents = new ArrayList<>();
        this.mongoCollection.find(type).forEach(documents::add);
        return documents;
    }

    @Override
    public void updateById(Object id, Bson bson) {
        this.mongoCollection.updateById(id, bson);
    }
}
