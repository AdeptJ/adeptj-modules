package com.adeptj.modules.data.mongodb.internal;

import com.adeptj.modules.data.mongodb.BaseDocument;
import com.adeptj.modules.data.mongodb.MongoRepository;
import com.adeptj.modules.data.mongodb.core.AbstractMongoRepository;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mongojack.JacksonMongoCollection;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adeptj.modules.data.mongodb.MongoConstants.DELIMITER_COLON;
import static com.adeptj.modules.data.mongodb.MongoConstants.KEY_COLLECTION_NAME;
import static com.adeptj.modules.data.mongodb.MongoConstants.KEY_DB_NAME;
import static com.adeptj.modules.data.mongodb.MongoConstants.SERVICE_FILTER;
import static org.bson.UuidRepresentation.STANDARD;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = MongoClientConfig.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class MongoClientLifecycle {

    private final MongoClient mongoClient;

    @Activate
    public MongoClientLifecycle(@NotNull MongoClientConfig config) {
        String username = config.auth_username();
        String database = config.auth_database();
        String password = config.auth_password();
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        if (StringUtils.isNoneEmpty(username, database, password)) {
            builder.credential(MongoCredential.createCredential(username, database, password.toCharArray()));
        }
        List<ServerAddress> serverAddresses = Stream.of(config.server_addresses())
                .map(row -> row.split(DELIMITER_COLON))
                .map(mapping -> new ServerAddress(mapping[0], Integer.parseInt(mapping[1])))
                .collect(Collectors.toList());
        MongoClientSettings clientSettings = builder.applyToSslSettings(b -> b.enabled(config.tls_enabled()))
                .applyToClusterSettings(b -> b.hosts(serverAddresses).build())
                .build();
        this.mongoClient = MongoClients.create(clientSettings);
    }

    @Deactivate
    protected void stop() {
        this.mongoClient.close();
    }

    @Reference(service = MongoRepository.class, target = SERVICE_FILTER, cardinality = MULTIPLE, policy = DYNAMIC)
    protected <T extends BaseDocument> void bindMongoRepository(@NotNull MongoRepository<T> mongoRepository,
                                                                @NotNull Map<String, String> properties) {
        String databaseName = properties.get(KEY_DB_NAME);
        String collectionName = properties.get(KEY_COLLECTION_NAME);
        AbstractMongoRepository<T> repository = (AbstractMongoRepository<T>) mongoRepository;
        Class<T> documentClass = repository.getDocumentClass();
        if (documentClass == null) {
            throw new MongoRepositoryBindException("AbstractMongoRepository implementation must provide the DocumentClass!");
        }
        repository.setMongoCollection(JacksonMongoCollection.builder()
                .build(this.mongoClient, databaseName, collectionName, documentClass, STANDARD));
    }

    protected <T extends BaseDocument> void unbindMongoRepository(@NotNull MongoRepository<T> mongoRepository) {
        AbstractMongoRepository<T> repository = (AbstractMongoRepository<T>) mongoRepository;
        repository.setMongoCollection(null);
    }
}
