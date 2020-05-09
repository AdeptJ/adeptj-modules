package com.adeptj.modules.data.mongodb;

public final class MongoConstants {

    public static final String DELIMITER_COLON = ":";

    public static final String KEY_DB_NAME = "mongodb.database.name";

    public static final String KEY_COLLECTION_NAME = "mongodb.collection.name";

    public static final String SERVICE_FILTER = "(&(mongodb.database.name=*)(mongodb.collection.name=*))";
}
