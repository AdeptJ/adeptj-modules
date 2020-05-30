package com.adeptj.modules.data.mongodb;

import org.bson.conversions.Bson;

import java.util.List;

public interface MongoRepository<T> {

    void insert(T object);

    T findOneById(Object id);

    List<T> findAll(Class<T> type);

    void updateById(Object id, Bson bson);
}
