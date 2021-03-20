package com.adeptj.modules.data.mongodb;

import com.mongodb.client.MongoClient;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface MongoClientProvider {

    MongoClient getMongoClient();
}
