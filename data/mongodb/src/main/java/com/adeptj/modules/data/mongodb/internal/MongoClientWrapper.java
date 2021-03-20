package com.adeptj.modules.data.mongodb.internal;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.connection.ClusterDescription;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class MongoClientWrapper implements MongoClient {

    private final MongoClient delegate;

    public MongoClientWrapper(MongoClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public MongoDatabase getDatabase(String databaseName) {
        return this.delegate.getDatabase(databaseName);
    }

    @Override
    public ClientSession startSession() {
        return this.delegate.startSession();
    }

    @Override
    public ClientSession startSession(ClientSessionOptions options) {
        return this.delegate.startSession(options);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("MongoClient can't be closed!!");
    }

    @Override
    public MongoIterable<String> listDatabaseNames() {
        return this.delegate.listDatabaseNames();
    }

    @Override
    public MongoIterable<String> listDatabaseNames(ClientSession clientSession) {
        return this.delegate.listDatabaseNames(clientSession);
    }

    @Override
    public ListDatabasesIterable<Document> listDatabases() {
        return this.delegate.listDatabases();
    }

    @Override
    public ListDatabasesIterable<Document> listDatabases(ClientSession clientSession) {
        return this.delegate.listDatabases(clientSession);
    }

    @Override
    public <TResult> ListDatabasesIterable<TResult> listDatabases(Class<TResult> tResultClass) {
        return this.delegate.listDatabases(tResultClass);
    }

    @Override
    public <TResult> ListDatabasesIterable<TResult> listDatabases(ClientSession clientSession, Class<TResult> tResultClass) {
        return this.delegate.listDatabases(clientSession, tResultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch() {
        return this.delegate.watch();
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
        return this.delegate.watch(tResultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(List<? extends Bson> pipeline) {
        return this.delegate.watch(pipeline);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return this.delegate.watch(pipeline, tResultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
        return this.delegate.watch(clientSession);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> tResultClass) {
        return this.delegate.watch(clientSession, tResultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession, List<? extends Bson> pipeline) {
        return this.delegate.watch(clientSession, pipeline);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return this.delegate.watch(clientSession, pipeline, tResultClass);
    }

    @Override
    public ClusterDescription getClusterDescription() {
        return this.delegate.getClusterDescription();
    }
}
