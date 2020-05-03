package com.adeptj.modules.data.sql2o.core;

import com.adeptj.modules.data.sql2o.NamedParam;
import com.adeptj.modules.data.sql2o.Sql2oRepository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public abstract class AbstractSql2oRepository<T, K> implements Sql2oRepository<T, K> {

    private Sql2o sql2o;

    protected Sql2o getSql2o() {
        return sql2o;
    }

    public void setSql2o(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public int insert(String queryText, T bindObject) {
        int result = 0;
        Connection connection = null;
        try {
            connection = this.getSql2o().beginTransaction();
            result = connection.createQuery(queryText).bind(bindObject).executeUpdate().getResult();
            connection.commit(true);
        } catch (Sql2oException ex) {
            if (connection != null) {
                connection.rollback(true);
            }
        }
        return result;
    }

    @Override
    public K insert(String queryText, T bindObject, Class<K> keyType) {
        K result = null;
        Connection connection = null;
        try {
            connection = this.getSql2o().beginTransaction();
            result = connection.createQuery(queryText, true).bind(bindObject).executeUpdate()
                    .getKey(keyType);
            connection.commit(true);
        } catch (Sql2oException ex) {
            if (connection != null) {
                connection.rollback(true);
            }
        }
        return result;
    }

    @Override
    public int update(String queryText, NamedParam... params) {
        int result = 0;
        Connection connection = null;
        try {
            connection = this.getSql2o().beginTransaction();
            Query query = connection.createQuery(queryText);
            for (NamedParam parameter : params) {
                query.addParameter(parameter.getName(), parameter.getValue());
            }
            result = query.executeUpdate().getResult();
            connection.commit(true);
        } catch (Sql2oException ex) {
            if (connection != null) {
                connection.rollback(true);
            }
        }
        return result;
    }

    @Override
    public int delete(String queryText, NamedParam... params) {
        int result = 0;
        Connection connection = null;
        try {
            connection = this.getSql2o().beginTransaction();
            Query query = connection.createQuery(queryText);
            for (NamedParam parameter : params) {
                query.addParameter(parameter.getName(), parameter.getValue());
            }
            result = query.executeUpdate().getResult();
            connection.commit(true);
        } catch (Sql2oException ex) {
            if (connection != null) {
                connection.rollback(true);
            }
        }
        return result;
    }

    @Override
    public List<T> find(Class<T> type, String queryText, NamedParam... params) {
        try (Connection connection = this.getSql2o().open()) {
            Query query = connection.createQuery(queryText);
            for (NamedParam parameter : params) {
                query.addParameter(parameter.getName(), parameter.getValue());
            }
            return query.executeAndFetch(type);
        }
    }

    @Override
    public T findOne(Class<T> type, String queryText, NamedParam... params) {
        try (Connection connection = this.getSql2o().open()) {
            Query query = connection.createQuery(queryText);
            for (NamedParam parameter : params) {
                query.addParameter(parameter.getName(), parameter.getValue());
            }
            return query.executeAndFetchFirst(type);
        }
    }
}
