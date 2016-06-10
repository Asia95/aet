/*
 * Cognifide AET :: Version Storage
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.vs.mongodb;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Service(MongoDBClient.class)
@Component(label = "AET MongoDB Client", description = "AET MongoDB Client", immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
public class MongoDBClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBClient.class);

	private static final String MONGO_URI = "MongoURI";

	public static final String DEFAULT_MONGODB_URI = "mongodb://localhost";

	public static final boolean DEFAULT_AUTOCREATE_VALUE = false;

	@Property(name = MONGO_URI, label = "MongoURI", description = "mongodb://[username:password@]host1[:port1][,host2[:port2],"
			+ "...[,hostN[:portN]]][/[database][?options]]", value = DEFAULT_MONGODB_URI)
	private String mongoUri;

	private static final String ALLOW_AUTO_CREATE = "AllowAutoCreate";

	@Property(name = ALLOW_AUTO_CREATE, label = "Allow automatic creation of DB", description = "Allows automatic creation of DB if set to true", boolValue = DEFAULT_AUTOCREATE_VALUE)
	private Boolean allowAutoCreate;

	private MongoClient mongoClient;

	public MongoClient getMongoClient() {
		return mongoClient;
	}

	@Activate
	public void activate(Map properties) {
		setupProperties(properties);
		try {
			setupMongoDBConnection();
		} catch (Exception e) {
			this.deactivate();
			LOGGER.error("Unable to connect to Mongo instance", e);
		}
	}

	@Deactivate
	public void deactivate() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	/**
	 * Get collection of companies in AET system
	 *
	 * @return collection of companies names unless database names matches convention: $company_$project and
	 * $company dosen't contain any underscrore characters
	 */
	public Collection<String> getCompanies() {
		final Collection<String> companies = new LinkedList<String>();

		for (String dbName : getAetsDBNames()) {
			if (!StringUtils.containsAny(dbName, "_")) {
				LOGGER.error("Database name format is incorrect. It must contain at least one underscore character. Couldn't fetch company name from database name. Skip.");
			}
			String companyName = StringUtils.substringBefore(dbName, "_");
			if (StringUtils.isBlank(companyName)) {
				LOGGER.error("Comapny name is blank. It couldn't've been fetched from database name [{}] ",
						dbName);
			} else if (StringUtils.isNotBlank(companyName)) {
				companies.add(companyName);
			}

		}

		return companies;
	}

	private void setupProperties(Map properties) {
		this.mongoUri = PropertiesUtil.toString(properties.get(MONGO_URI), DEFAULT_MONGODB_URI);
		this.allowAutoCreate = PropertiesUtil.toBoolean(properties.get(ALLOW_AUTO_CREATE),
				DEFAULT_AUTOCREATE_VALUE);
	}

	private void setupMongoDBConnection() throws UnknownHostException {
		mongoClient = new MongoClient(new MongoClientURI(mongoUri));
		testConnection();
		LOGGER.info("Mongo client connected at: " + mongoUri);
	}

	private void testConnection() {
		mongoClient.getDatabaseNames();
	}

	/**
	 * Gets MonogoDB database object. It does not auto-creates database if given db name does not corresponds
	 * to any db's.
	 *
	 * @param dbName name of database
	 * @return MongoDB databaase object or null if base does not exists.
	 */
	public DB getDB(String dbName) {
		return getDB(dbName, false);
	}

	/**
	 * Gets MonogoDB database object. Given that autoCreate param is set to true it will auto-create database
	 * with provided name (if one does not exists yet).
	 *
	 * @param dbName name of database
	 * @param autoCreate defines if database creation auto-creation should occur
	 * @return MongoDB databaase object or null if base does not exists.
	 */
	public DB getDB(String dbName, Boolean autoCreate) {
		DB db = null;
		String lowerCaseDbName = dbName.toLowerCase();
		if ((allowAutoCreate && autoCreate) || getAetsDBNames().contains(lowerCaseDbName)) {
			db = mongoClient.getDB(lowerCaseDbName);
		}
		return db;
	}

	/**
	 * Gets collection of database names that are AET system specific
	 *
	 * @return collection of db names
	 */
	public Collection<String> getAetsDBNames() {
		final Collection<String> databaseNames = new LinkedList<String>();

		for (String dbName : mongoClient.listDatabaseNames()) {
			if (!(dbName.charAt(0) == '_') && !"local".equals(dbName) && !"admin".equals(dbName)) {
				databaseNames.add(dbName);
			}
		}

		return databaseNames;
	}

	/**
	 * Checks if database contains specified collection.
	 *
	 * @param dbName name of database
	 * @param collectionName name of collection
	 *
	 * @return true if database contains specified collection.
	 */
	public boolean hasCollection(String dbName, String collectionName) {
		return getCollectionsList(dbName).contains(collectionName);
	}

	private Set<String> getCollectionsList(String dbName) {
		Set<String> result = Collections.emptySet();
		DB db = getDB(dbName);
		if (db != null) {
			result = db.getCollectionNames();
		}

		return result;
	}

}