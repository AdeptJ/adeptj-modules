package com.adeptj.modules.data.mongodb.core;

import com.adeptj.modules.data.mongodb.BaseDocument;
import com.adeptj.modules.data.mongodb.MongoRepository;
import org.bson.conversions.Bson;
import org.mongojack.JacksonMongoCollection;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMongoRepository<T extends BaseDocument> implements MongoRepository<T> {

    private JacksonMongoCollection<T> mongoCollection;

    protected JacksonMongoCollection<T> getMongoCollection() {
        return mongoCollection;
    }

    public void setMongoCollection(JacksonMongoCollection<T> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    public abstract Class<T> getDocumentClass();

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
        this.getMongoCollection().find(type).cursor().forEachRemaining(documents::add);
        return documents;
    }

    @Override
    public void updateById(Object id, Bson bson) {
        this.getMongoCollection().updateById(id, bson);
    }
}
