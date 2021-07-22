/*
 * File: BaseMongoTest.java
 * Creation Date: Jun 19, 2021
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
package com.tnsilver.contacts.base;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.tnsilver.contacts.model.Audit;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.Sequence;

@ActiveProfiles({ "test" })
@DirtiesContext
public abstract class BaseMongoTest extends BaseTest {

    protected static final String CONNECTION_STRING = "mongodb://localhost:27017/contacts";
    protected static MongoClient mongoClient;

    @Value("${mongo.populator.location}")
    String location;
    @Autowired
    protected MongoTemplate mongoTemplate;

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseTest.beforeAll();
        String[] profiles = BaseMongoTest.class.getDeclaredAnnotation(ActiveProfiles.class).value();
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, Arrays.stream(profiles).collect(Collectors.joining(",")));
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    protected static final MongoClient getMongoClient(String connectionString) {
        return MongoClients
            .create(MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString)).build());
    }

    /**
     * drops the collections for Contact.Audit and Sequence, if they exist
     */
    protected static void clearDatabase() {
        mongoClient = getMongoClient(CONNECTION_STRING);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, DATABASE);
        boolean exists = mongoTemplate.collectionExists(Contact.class);
        long count = 0;
        if (exists) {
            count = mongoTemplate.count(Query.query(Criteria.where("_id").exists(true)), Contact.class);
            if (count > 0) {
                Stream.of(Sequence.class, Audit.class, Contact.class).forEach(clazz -> {
                    if (mongoTemplate.collectionExists(clazz)) {
                        mongoTemplate.dropCollection(clazz);
                        System.out.printf("BaseTest.clearDatabase() -> collection of class %s dropped!%n", clazz.getSimpleName());
                    }
                });

            }
        }
    }

    /*protected void initDatabase() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));
        long count = mongoTemplate.count(Query.query(Criteria.where("ssn").exists(true)), Contact.class);
        if (count > 0) {
            System.out.printf("%BaseTest.initDatabase() -> COLLECTION NOT EMPTY! will not populate database!%n");
            return;
        }
        System.out.printf("%BaseTest.initDatabase() -> POPULATING COLLECTION 'contacts'...!%n");
        File jsonFile = new ClassPathResource(location).getFile();
        List<Contact> contacts = Arrays.asList(mapper.readValue(jsonFile, Contact[].class));
        Assert.notNull(contacts, String.format("cannot read data from '%s'", jsonFile.getPath()));
        long size = contacts.size();
        Assert.isTrue(size == NUM_OF_CONTACTS, String.format("found %d contacts but expected %d.", size, NUM_OF_CONTACTS));
        mongoTemplate.insert(contacts, Contact.class);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Z")));
        System.out.printf("%BaseTest.initDatabase() -> POPULATED COLLECTION!%n");
    }*/

}
