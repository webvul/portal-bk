package com.kii.beehive.portal.store;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

//@Configuration
//@EnableMongoRepositories("com.kii.beehive.portal.store.repositories")
public class StoreSpringConfig extends AbstractMongoConfiguration {

	@Value("${portal.mongo.port}")
	private int port;

	@Value("${portal.mongo.host}")
	private String host;



	@Override
	protected String getDatabaseName() {
		return "portal-store";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(host,port);
	}

	@Override
	protected String getMappingBasePackage() {
		return "com.kii.beehive.portal.store.entity";
	}
}
