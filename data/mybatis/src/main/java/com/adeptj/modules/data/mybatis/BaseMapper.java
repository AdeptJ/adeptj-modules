package com.adeptj.modules.data.mybatis;

import java.util.List;
import java.util.Map;

public interface BaseMapper<T> {

    T findById(Object id);

    T find(Map<String, Object> queryParams);

    List<T> findMany(Map<String, Object> queryParams);

    List<T> findAll();

    void insert(T object);

    void updateById(T object);

    void updateMany(Map<String, Object> attributes, Map<String, Object> queryParams);

    void deleteById(Object id);

    void deleteMany(Map<String, Object> queryParams);
}
