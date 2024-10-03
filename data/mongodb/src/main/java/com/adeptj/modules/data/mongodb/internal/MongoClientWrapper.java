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
import org.jetbrains.annotations.NotNull;

import java.util.List;

class MongoClientWrapper implements MongoClient {

    private final MongoClient delegate;

    MongoClientWrapper(@NotNull MongoClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull MongoDatabase getDatabase(@NotNull String databaseName) {
        return this.delegate.getDatabase(databaseName);
    }

    @Override
    public @NotNull ClientSession startSession() {
        return this.delegate.startSession();
    }

    @Override
    public @NotNull ClientSession startSession(@NotNull ClientSessionOptions options) {
        return this.delegate.startSession(options);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("MongoClient can't be closed by the consumer!!");
    }

    @Override
    public @NotNull MongoIterable<String> listDatabaseNames() {
        return this.delegate.listDatabaseNames();
    }

    @Override
    public @NotNull MongoIterable<String> listDatabaseNames(@NotNull ClientSession clientSession) {
        return this.delegate.listDatabaseNames(clientSession);
    }

    @Override
    public @NotNull ListDatabasesIterable<Document> listDatabases() {
        return this.delegate.listDatabases();
    }

    @Override
    public @NotNull ListDatabasesIterable<Document> listDatabases(@NotNull ClientSession clientSession) {
        return this.delegate.listDatabases(clientSession);
    }

    @Override
    public <TResult> @NotNull ListDatabasesIterable<TResult> listDatabases(@NotNull Class<TResult> tResultClass) {
        return this.delegate.listDatabases(tResultClass);
    }

    @Override
    public <TResult> @NotNull ListDatabasesIterable<TResult> listDatabases(@NotNull ClientSession clientSession,
                                                                           @NotNull Class<TResult> tResultClass) {
        return this.delegate.listDatabases(clientSession, tResultClass);
    }

    @Override
    public @NotNull ChangeStreamIterable<Document> watch() {
        return this.delegate.watch();
    }

    @Override
    public <TResult> @NotNull ChangeStreamIterable<TResult> watch(@NotNull Class<TResult> tResultClass) {
        return this.delegate.watch(tResultClass);
    }

    @Override
    public @NotNull ChangeStreamIterable<Document> watch(@NotNull List<? extends Bson> pipeline) {
        return this.delegate.watch(pipeline);
    }

    @Override
    public <TResult> @NotNull ChangeStreamIterable<TResult> watch(@NotNull List<? extends Bson> pipeline,
                                                                  @NotNull Class<TResult> tResultClass) {
        return this.delegate.watch(pipeline, tResultClass);
    }

    @Override
    public @NotNull ChangeStreamIterable<Document> watch(@NotNull ClientSession clientSession) {
        return this.delegate.watch(clientSession);
    }

    @Override
    public <TResult> @NotNull ChangeStreamIterable<TResult> watch(@NotNull ClientSession clientSession,
                                                                  @NotNull Class<TResult> tResultClass) {
        return this.delegate.watch(clientSession, tResultClass);
    }

    @Override
    public @NotNull ChangeStreamIterable<Document> watch(@NotNull ClientSession clientSession,
                                                         @NotNull List<? extends Bson> pipeline) {
        return this.delegate.watch(clientSession, pipeline);
    }

    @Override
    public <TResult> @NotNull ChangeStreamIterable<TResult> watch(@NotNull ClientSession clientSession,
                                                                  @NotNull List<? extends Bson> pipeline,
                                                                  @NotNull Class<TResult> tResultClass) {
        return this.delegate.watch(clientSession, pipeline, tResultClass);
    }

    @Override
    public @NotNull ClusterDescription getClusterDescription() {
        return this.delegate.getClusterDescription();
    }
}
