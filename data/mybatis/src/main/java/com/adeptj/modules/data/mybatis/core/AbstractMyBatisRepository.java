package com.adeptj.modules.data.mybatis.core;

import com.adeptj.modules.data.mybatis.BaseMapper;
import com.adeptj.modules.data.mybatis.MyBatisRepository;
import org.apache.commons.lang3.Validate;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ConsumerType
public abstract class AbstractMyBatisRepository<T, ID> implements MyBatisRepository<T, ID> {

    private SqlSessionFactory sessionFactory;

    protected SqlSessionFactory getSessionFactory() {
        Validate.validState(this.sessionFactory != null, "SqlSessionFactory is null!");
        return this.sessionFactory;
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public T findById(String statement, ID id) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            return session.selectOne(statement, id);
        }
    }

    @Override
    public T findById(Class<? extends BaseMapper<T, ID>> mapper, ID id) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            return session.getMapper(mapper).findById(id);
        }
    }

    @Override
    public T findOne(Class<? extends BaseMapper<T, ID>> mapper, Map<String, Object> queryParams) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            return session.getMapper(mapper).findOne(queryParams);
        }
    }

    @Override
    public List<T> findAll(String statement) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            return session.selectList(statement);
        }
    }

    @Override
    public List<T> findAll(Class<? extends BaseMapper<T, ID>> mapper) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            return session.getMapper(mapper).findAll();
        }
    }

    @Override
    public void insert(String statement, T object) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            session.insert(statement, object);
            session.commit();
        }
    }

    @Override
    public void insert(Class<? extends BaseMapper<T, ID>> mapper, T object) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            session.getMapper(mapper).insert(object);
            session.commit();
        }
    }

    @Override
    public void deleteById(String statement, ID id) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            session.delete(statement, id);
            session.commit();
        }
    }

    @Override
    public void deleteById(Class<? extends BaseMapper<T, ID>> mapper, ID id) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            session.getMapper(mapper).deleteById(id);
            session.commit();
        }
    }

    @Override
    public void update(String statement, T object) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            session.update(statement, object);
            session.commit();
        }
    }

    @Override
    public void update(Class<? extends BaseMapper<T, ID>> mapper, T object) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            session.getMapper(mapper).update(object);
            session.commit();
        }
    }

    @Override
    public <E> E doInSession(@NotNull Function<SqlSession, E> function) {
        try (SqlSession session = this.getSessionFactory().openSession()) {
            return function.apply(session);
        }
    }
}
