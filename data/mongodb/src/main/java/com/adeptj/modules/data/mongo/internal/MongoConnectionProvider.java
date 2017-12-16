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

import static com.adeptj.modules.data.mongo.internal.MongoConnectionProvider.COMPONENT_NAME;
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
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class MongoConnectionProvider implements ManagedServiceFactory {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MongoConnectionProvider.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.data.mongo.MongoConnectionProvider.factory";

    private static final String FACTORY_NAME = "AdeptJ MongoDB Connection Provider";

    private Map<String, MongoCrudRepository> serviceContainer = new HashMap<>();

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String s, Dictionary<String, ?> properties) throws ConfigurationException {
        this.buildCrudService(properties, s);
    }

    @Override
    public void deleted(String s) {
        LOGGER.info("inside mongo delete");
    }

    private void buildCrudService(Dictionary<String, ?> properties, String id) {
        try {
            MongoCredential credential = null;
            if (PropertiesUtil.toString(properties.get("username"), null) != null) {
                credential = MongoCredential.createCredential(
                        PropertiesUtil.toString(properties.get("username"), ""),
                        PropertiesUtil.toString(properties.get("dbName"), ""),
                        PropertiesUtil.toString(properties.get("password"), "").toCharArray()
                );
            }

            Morphia morphia = new Morphia();
            if (PropertiesUtil.toString(properties.get("mappablePackage"), null) != null) {
                morphia.mapPackage(properties.get("mappablePackage").toString());
            }

            ServerAddress serverAddress = new ServerAddress(
                    PropertiesUtil.toString(properties.get("hostName"), ""),
                    PropertiesUtil.toInteger(properties.get("hostName"), 0)
            );

            MongoClient mongoClient = new MongoClient(
                serverAddress,
                Arrays.asList(credential),
                this.buildMongoOptions(properties)
            );

            this.serviceContainer.put(id, new MongoCrudRepositoryImpl(
                    morphia.createDatastore(
                            mongoClient,
                            PropertiesUtil.toString(properties.get("dbName"), "")
                    )
            ));
        } catch (Exception ex) {
            LOGGER.error("Unable to create mongo crud service for config id {} full exception ", id, ex);
        }
    }

    private MongoClientOptions buildMongoOptions(Dictionary<String, ?> properties) {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        //TODO Mongo config for connection pool  configurations.
        return builder.build();
    }


}
