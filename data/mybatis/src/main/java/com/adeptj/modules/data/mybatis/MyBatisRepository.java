package com.adeptj.modules.data.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ConsumerType
public interface MyBatisRepository<T, ID> {

    T findById(String statement, ID id);

    T findById(Class<? extends BaseMapper<T, ID>> mapper, ID id);

    T findOne(Class<? extends BaseMapper<T, ID>> mapper, Map<String, Object> queryParams);

    List<T> findAll(String statement);

    List<T> findAll(Class<? extends BaseMapper<T, ID>> mapper);

    void insert(String statement, T object);

    void insert(Class<? extends BaseMapper<T, ID>> mapper, T object);

    void deleteById(String statement, ID id);

    void update(String statement, T object);

    <E> E doInSession(@NotNull Function<SqlSession, E> function);
}
