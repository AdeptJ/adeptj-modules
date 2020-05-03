package com.adeptj.modules.data.sql2o;

import java.util.List;

public interface Sql2oRepository<T, K> {

    int insert(String queryText, T bindObject);

    K insert(String queryText, T bindObject, Class<K> keyType);

    int update(String queryText, NamedParam... params);

    int delete(String queryText, NamedParam... params);

    List<T> find(Class<T> type, String queryText, NamedParam... params);

    T findOne(Class<T> type, String queryText, NamedParam... params);
}
