/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.data.mongo.internal;

import com.adeptj.modules.commons.utils.PropertiesUtil;
import com.adeptj.modules.data.mongo.api.MongoCrudRepository;
import com.adeptj.modules.data.mongo.exception.InvalidMongoDatabaseException;
import com.adeptj.modules.data.mongo.exception.InvalidMongoUnitException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Morphia;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.adeptj.modules.data.mongo.internal.Utils.PROVIDER_COMPONENT_NAME;
import static com.adeptj.modules.data.mongo.internal.Utils.PROVIDER_FACTORY_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * Managed service factory to manage MongoDB connections as OSGI configurations.
 *
 * @author prince.arora, AdeptJ.
 */
@Designate(ocd = MongoConfiguration.class, factory = true)
@Component(
        immediate = true,
        name = PROVIDER_COMPONENT_NAME,
        property = SERVICE_PID + "=" + PROVIDER_COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class MongoConnectionProvider implements ManagedServiceFactory {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MongoConnectionProvider.class);

    private Map<String, MongoCrudRepository> serviceContainer = new HashMap<>();

    private Map<String, String> unitMapping = new HashMap<>();

    private static final String UNITNAME_PROP = "unitName";

    @Override
    public String getName() {
        return PROVIDER_FACTORY_NAME;
    }

    @Override
    public void updated(String s, Dictionary<String, ?> properties) throws ConfigurationException {
        this.buildCrudService(properties, s);
    }

    @Override
    public void deleted(String id) {
        this.destroy(id);
    }

    private void buildCrudService(Dictionary<String, ?> properties, String id) {
        try {
            if (!PropertiesUtil.toString(properties.get(UNITNAME_PROP), "").equals("")) {
                //Aboard in case of nothing provided in dbname.
                //Avoiding db connection creation with blank db name.
                if (PropertiesUtil.toString(properties.get("dbName"), "").equals("")) {
                    throw new InvalidMongoDatabaseException("Invalid mongo database name. Provide a valid database name.");
                }
                MongoCredential credential = null;
                if (!PropertiesUtil.toString(properties.get("username"), "").equals("")) {
                    credential = MongoCredential.createCredential(
                            PropertiesUtil.toString(properties.get("username"), ""),
                            PropertiesUtil.toString(properties.get("dbName"), ""),
                            PropertiesUtil.toString(properties.get("password"), "").toCharArray()
                    );
                }

                Morphia morphia = new Morphia();
                if (!PropertiesUtil.toString(properties.get("mappablePackage"), "").equals("")) {
                    morphia.mapPackage(properties.get("mappablePackage").toString());
                }

                ServerAddress serverAddress = new ServerAddress(
                        PropertiesUtil.toString(properties.get("hostName"), ""),
                        PropertiesUtil.toInteger(properties.get("port"), 0)
                );

                MongoClient mongoClient = null;
                if (credential != null) {
                    mongoClient = new MongoClient(
                            serverAddress,
                            Arrays.asList(credential),
                            this.buildMongoOptions(properties)
                    );
                } else {
                    mongoClient = new MongoClient(
                            serverAddress,
                            this.buildMongoOptions(properties)
                    );
                }

                if (this.unitMapping.containsKey(id)) {
                    this.destroy(id);
                }

                this.serviceContainer.put(
                        PropertiesUtil.toString(properties.get(UNITNAME_PROP), ""),
                        new MongoCrudRepositoryImpl(
                                morphia.createDatastore(
                                        mongoClient,
                                        PropertiesUtil.toString(properties.get("dbName"), "")
                                )
                        )
                );

                this.unitMapping.put(id,
                        PropertiesUtil.toString(properties.get(UNITNAME_PROP), ""));
            } else {
                throw new InvalidMongoUnitException("Invalid Unit name provided for mongo db configuration. Provide Valid and unique name for unit");
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to create mongo crud service for config id {} full exception ", id, ex);
        }
    }

    private MongoClientOptions buildMongoOptions(Dictionary<String, ?> properties) {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();

        //Read preference for mongodb
        builder.readPreference(
                Objects.requireNonNull(Utils.readPreference(
                        Stream.of(ReadPreferenceEnum.values()).filter(
                                (readPreferenceEnum) -> {
                                    return readPreferenceEnum.name()
                                            .equals(PropertiesUtil.toString(properties.get("readPreference"), ""));
                                }
                        ).findFirst().orElse(ReadPreferenceEnum.NEAREST)
                ))
        );

        //Write concern for mongodb
        builder.writeConcern(
                Objects.requireNonNull(Utils.writeConcern(
                        Stream.of(WriteConcernEnum.values()).filter(
                                (writeConcernEnum) -> {
                                    return writeConcernEnum
                                            .equals(PropertiesUtil.toString(properties.get("readPreference"), ""));
                                }
                        ).findFirst().orElse(WriteConcernEnum.UNACKNOWLEDGED)
                ))
        );

        builder.serverSelectionTimeout(
                PropertiesUtil.toInteger(properties.get("serverSelectionTimeout"), 30000)
        );
        builder.connectionsPerHost(
                PropertiesUtil.toInteger(properties.get("maxConnectionsPerHost"), 100)
        );
        builder.maxWaitTime(
                PropertiesUtil.toInteger(properties.get("maxConnectionsPerHost"), 120000)
        );
        builder.maxConnectionIdleTime(
                PropertiesUtil.toInteger(properties.get("maxConnectionIdleTime"), 100000)
        );
        builder.sslEnabled(
                PropertiesUtil.toBoolean(properties.get("sslEnabled"), false)
        );
        builder.connectTimeout(
                PropertiesUtil.toInteger(properties.get("connectTimeout"), 30000)
        );
        builder.maxConnectionLifeTime(
                PropertiesUtil.toInteger(properties.get("maxConnectionLifeTime"), 300000)
        );
        return builder.build();
    }

    private void destroy(String id) {
        if (this.unitMapping.containsKey(id)) {
            //Closing connection to mongodb server for delete action
            //for any configuration.
            Optional.of(this.serviceContainer.remove(this.unitMapping.get(id)))
                    .ifPresent(crudRepository -> {
                        LOGGER.debug("Closing connection for unit {}", unitMapping.get(id));
                        crudRepository.getDatastore().getMongo().close();
                        crudRepository = null;
                        unitMapping.remove(id);
                    });
        }
    }

    /**
     * Returns {@link Optional} of {@link MongoCrudRepository} for a given unit name.
     * <p>
     * Unit name should be same as given in mongoDB connection factory configuration.
     * An empty {@link Optional} will be returned if unitName does not match any configuration.
     *
     * @param unitName  string identifier for mongodb configuration.
     * @return  {@link Optional<MongoCrudRepository>}
     */
    public Optional<MongoCrudRepository> getRepository(String unitName) {
        return this.serviceContainer.containsValue(unitName) ?
                        Optional.of(this.serviceContainer.get(unitName)) :
                        Optional.empty();

    }


}
