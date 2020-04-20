package com.adeptj.modules.data.sql2o.core;

import com.adeptj.modules.data.sql2o.Sql2oRepository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class AbstractSql2oRepository implements Sql2oRepository {

    private Sql2o sql2o;

    protected Sql2o getSql2o() {
        return sql2o;
    }

    public void setSql2o(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public <T> List<T> find(Class<T> type, String query) {
        try (Connection connection = this.getSql2o().open()) {
            return connection.createQuery(query).executeAndFetch(type);
        }
    }
}
