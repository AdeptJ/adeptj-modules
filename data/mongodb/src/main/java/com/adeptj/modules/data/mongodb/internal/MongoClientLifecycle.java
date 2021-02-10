package com.adeptj.modules.data.mongodb.internal;

import com.adeptj.modules.data.mongodb.MongoRepository;
import com.adeptj.modules.data.mongodb.core.AbstractMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mongojack.JacksonMongoCollection;
import org.mongojack.ObjectMapperConfigurer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adeptj.modules.data.mongodb.MongoConstants.DELIMITER_COLON;
import static com.adeptj.modules.data.mongodb.MongoConstants.KEY_COLLECTION_NAME;
import static com.adeptj.modules.data.mongodb.MongoConstants.KEY_DB_NAME;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static org.bson.UuidRepresentation.STANDARD;
import static org.mongojack.JacksonMongoCollection.JacksonMongoCollectionBuilder;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = MongoClientConfig.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class MongoClientLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String SERVICE_FILTER = "(&(mongodb.database.name=*)(mongodb.collection.name=*))";

    private final MongoClient mongoClient;

    private final JacksonMongoCollectionBuilder mongoCollectionBuilder;

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
                .map(parts -> new ServerAddress(parts[0], Integer.parseInt(parts[1])))
                .collect(Collectors.toList());
        MongoClientSettings clientSettings = builder.applyToSslSettings(b -> b.enabled(config.tls_enabled()))
                .applyToClusterSettings(b -> b.hosts(serverAddresses).build())
                .build();
        this.mongoClient = MongoClients.create(clientSettings);
        // see - https://github.com/mongojack/mongojack/issues/202
        this.mongoCollectionBuilder = JacksonMongoCollection.builder()
                .withObjectMapper(ObjectMapperConfigurer.configureObjectMapper(new ObjectMapper()
                        .enable(INDENT_OUTPUT)
                        .setDefaultPropertyInclusion(NON_DEFAULT)));
        LOGGER.info("MongoClient initialized!");
    }

    @Deactivate
    protected void stop() {
        this.mongoClient.close();
        LOGGER.info("MongoClient closed!");
    }

    @Reference(service = MongoRepository.class, target = SERVICE_FILTER, cardinality = MULTIPLE, policy = DYNAMIC)
    protected <T> void bindMongoRepository(@NotNull MongoRepository<T> repository, @NotNull Map<String, Object> properties) {
        // We are not interested in any of the MongoRepository impl which is not a subclass of AbstractMongoRepository.
        if (!(repository instanceof AbstractMongoRepository)) {
            throw new MongoRepositoryBindException("The repository instance must extend AbstractMongoRepository!");
        }
        String databaseName = (String) properties.get(KEY_DB_NAME);
        String collectionName = (String) properties.get(KEY_COLLECTION_NAME);
        if (StringUtils.isAnyEmpty(databaseName, collectionName)) {
            String message = String.format("MongoRepository service properties [%s] and [%s] can't be empty, " +
                            "please provide the mandatory non empty attributes of DocumentInfo annotation!",
                    KEY_DB_NAME, KEY_COLLECTION_NAME);
            throw new MongoRepositoryBindException(message);
        }
        LOGGER.info("Binding MongoRepository [{}]", repository);
        AbstractMongoRepository<T> mongoRepository = (AbstractMongoRepository<T>) repository;
        Class<T> documentClass = mongoRepository.getDocumentClass();
        LOGGER.info("Initializing JacksonMongoCollection for type [{}]", documentClass.getName());
        JacksonMongoCollection<T> mongoCollection = this.mongoCollectionBuilder
                .build(this.mongoClient, databaseName, collectionName, documentClass, STANDARD);
        mongoRepository.setMongoCollection(mongoCollection);
    }

    protected <T> void unbindMongoRepository(@NotNull MongoRepository<T> repository) {
        // Let's do an explicit type check to avoid a CCE.
        if (repository instanceof AbstractMongoRepository) {
            LOGGER.info("Unbinding MongoRepository [{}]", repository);
            AbstractMongoRepository<T> mongoRepository = (AbstractMongoRepository<T>) repository;
            mongoRepository.setMongoCollection(null);
        }
    }
}
