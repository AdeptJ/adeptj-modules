package com.adeptj.modules.data.mongodb.api;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;

import java.util.List;

public interface MongoRepository<T> {

    void insert(T document);

    void insertMany(List<T> documents);

    UpdateResult insertOrUpdate(T document);

    T findOneById(Object id);

    List<T> findMany();

    List<T> findMany(Bson filter);

    UpdateResult updateById(Object id, Bson bson);

    UpdateResult replaceOneById(Object id, T document);

    DeleteResult removeById(Object id);
}
