/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modularweb.persistence.repository.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.bson.Document;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adeptj.modularweb.persistence.model.Model;
import com.adeptj.modularweb.persistence.repository.api.CrudRepository;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

/**
 * MongoCrudRepository.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Service(CrudRepository.class)
@Component(metatype = true, immediate = true, label = "AdeptJ OSGi MongoCrudRepository")
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = "AdeptJ") })
public class MongoCrudRepository implements CrudRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoCrudRepository.class);

	@Property(label = "MongoDB Host", description = "Host where MongoDB is running", value = "127.0.0.1")
	public static final String MONGO_HOST = "mongodb.host";

	@Property(label = "MongoDB Port", description = "Port on MongoDB is running", value = "27017")
	public static final String MONGO_PORT = "mongodb.port";

	@Property(label = "MongoDB databse name", description = "Database name in MongoDB", value = "")
	public static final String MONGO_DB = "mongodb.dbname";

	@Property(label = "MongoDB Username", description = "MongoDB User for authenticating connections", value = "")
	public static final String MONGO_USERNAME = "mongodb.authuser";

	@Property(label = "MongoDB Password", description = "MongoDB Password for authenticating connections", value = "")
	public static final String MONGO_PASSWORD = "mongodb.authpwd";

	private String dbname;

	/**
	 * Per JVM single instance.
	 */
	private MongoClient mongoClient;

	/**
	 * Initialize {@link MongoClient}
	 */
	@Activate
	protected void activate(final Map<String, String> configs) {
		ServerAddress serverAddress = new ServerAddress(configs.get(MONGO_HOST),
				Integer.parseInt(configs.get(MONGO_PORT)));
		this.dbname = configs.get(MONGO_DB);
		MongoCredential mongoCredential = MongoCredential.createCredential(configs.get(MONGO_USERNAME), this.dbname,
				configs.get(MONGO_PASSWORD).toCharArray());
		this.mongoClient = new MongoClient(serverAddress, Arrays.asList(mongoCredential));
		LOGGER.info("Initialized MongoClient: {}", this.mongoClient);
	}

	/**
	 * Dispose {@link MongoClient}
	 */
	@Deactivate
	protected void deactivate() {
		if (this.mongoClient != null) {
			this.mongoClient.close();
			LOGGER.info("Closed MongoClient!");
		}
	}

	@Override
	public <T extends Model> void insert(Class<T> type, T document) {
	}

	@Override
	public <T extends Model> void update(Class<T> type, T document) {
		
	}

	@Override
	public <T extends Model> void delete(Class<T> type, T documentt) {
		
	}

	@Override
	public <T extends Model> List<T> findAll(Class<T> type) {
		return null;
	}

	protected <T extends Model> MongoCollection<Document> getCollection(T document) {
		return this.mongoClient.getDatabase(this.dbname).getCollection(document.getCollectionName());
	}

}