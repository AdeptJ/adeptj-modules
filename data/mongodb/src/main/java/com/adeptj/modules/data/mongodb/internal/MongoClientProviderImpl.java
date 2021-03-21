package com.adeptj.modules.data.mongodb.internal;

import com.adeptj.modules.data.mongodb.api.MongoClientProvider;
import com.mongodb.client.MongoClient;

public class MongoClientProviderImpl implements MongoClientProvider {

    private final MongoClient mongoClient;

    public MongoClientProviderImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public MongoClient getMongoClient() {
        return this.mongoClient;
    }
}
