package com.adeptj.modules.data.mongodb.api;

import com.mongodb.Function;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Consumer;

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

    T doWithMongoClient(Function<MongoClient, T> function);

    void doWithMongoClient(Consumer<MongoClient> consumer);
}
