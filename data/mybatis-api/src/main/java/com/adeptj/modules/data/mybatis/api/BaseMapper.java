package com.adeptj.modules.data.mybatis.api;

import java.util.List;
import java.util.Map;

public interface BaseMapper<T, ID> {

    T findById(ID id);

    T findOne(Map<String, Object> queryParams);

    List<T> findMany(Map<String, Object> queryParams);

    List<T> findAll();

    void insert(T object);

    void update(T object);

    void updateMany(Map<String, Object> attributes);

    void updateMany(Map<String, Object> attributes, Map<String, Object> queryParams);

    void deleteById(ID id);

    void deleteMany(Map<String, Object> queryParams);
}
