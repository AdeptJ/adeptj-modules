package com.adeptj.modules.data.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;
import java.util.function.Function;

@ConsumerType
public interface MyBatisRepository<T, ID> {

    T findById(String statement, ID id);

    List<T> findAll(String statement);

    void insert(String statement, T object);

    void deleteById(String statement, ID id);

    T doInSession(@NotNull Function<SqlSession, T> function);

    T doInSessionCommit(@NotNull Function<SqlSession, T> function);
}
