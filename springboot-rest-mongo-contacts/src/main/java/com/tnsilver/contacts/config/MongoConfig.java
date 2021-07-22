/*
 * File: MongoConfig.java
 * Creation Date: Jun 22, 2021
 *
 * Copyright (c) 2021 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses  this file to you under the Apache License, Version
 * 2.0 (the "License");  you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tnsilver.contacts.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.repository.ContactRepository;
import com.tnsilver.contacts.util.MongoRepositoryPopulatorFactory;

//@lombok.Generated // skip jacoco
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackageClasses = ContactRepository.class)
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Autowired
    private Environment env;
    @Value("${mongo.populator.location}")
    String location;
    @Value("${spring.data.mongodb.host}")
    String host;
    @Value("${spring.data.mongodb.port}")
    int port;
    @Value("${spring.data.mongodb.database}")
    String database;
    @Value("${spring.data.mongodb.username}")
    String username;
    @Value("${spring.data.mongodb.password}")
    String password;

    final static String AUTH_CONNECTION_STRING = "mongodb://%s:%s@%s:%d/%s";
    final static String CONNECTION_STRING = "mongodb://%s:%d/%s";

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean respositoryPopulator(MongoOperations mongoOperations) {
        MongoRepositoryPopulatorFactory factory = new MongoRepositoryPopulatorFactory(Contact.class, mongoOperations);
        factory.setResources(new Resource[] { new ClassPathResource(location) });
        return factory;
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        boolean prod = Arrays.stream(env.getActiveProfiles()).anyMatch(p -> p.equals("prod"));
        final ConnectionString connectionString;
        if (prod)
            connectionString = new ConnectionString(String.format(AUTH_CONNECTION_STRING, username, password, host, port, database));
        else
            connectionString = new ConnectionString(String.format(CONNECTION_STRING, host, port, database));
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.tnsilver.contacts.model");
    }

}
