/*
 * File: MongoRepositoryPopulatorFactory.java
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
package com.tnsilver.contacts.util;

import java.time.ZoneId;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import com.tnsilver.contacts.model.BaseDocument;
import com.tnsilver.contacts.model.Contact;

/**
 * This extension of Jackson2RepositoryPopulatorFactoryBean only reacts to {@code ContextRefreshedEvent} if
 * the MongoDB collection corresponding to Document {@code clazz} is empty.
 * If the collection corresponding to the given Document {@code clazz} is empty, this factory will use the
 * repository populator configured in {@link TestMongoConfig} class to load json data to the collection
 * specified in the configuration.
 *
 * @apiNote do not annotate this bean, there's no need to.
 *
 * @author T.N.Silverman
 *
 */
public class MongoRepositoryPopulatorFactory extends Jackson2RepositoryPopulatorFactoryBean {

    private final static Logger logger = LoggerFactory.getLogger(MongoRepositoryPopulatorFactory.class);

    private MongoOperations mongoOperations;
    private Class<? extends BaseDocument> clazz;

    public MongoRepositoryPopulatorFactory(Class<? extends BaseDocument> clazz, MongoOperations mongoOperations) {
        super();
        this.clazz = clazz;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.debug("checking {} collection exists...",clazz.getSimpleName());
        boolean exists = mongoOperations.collectionExists(clazz);
        long count = -1L;
        if (exists) {
            logger.debug("checking count {}...",clazz.getSimpleName());
            count = mongoOperations.count(Query.query(Criteria.where("_id").exists(true)), Contact.class);
            logger.debug("found {} {} id's",count,clazz.getSimpleName());
        }
        if (!exists || count == 0) {
            logger.debug("\n------------------------------\n LOADING {} resources...\n------------------------------",clazz.getSimpleName());
            /*
             * set the default time zone to the system default, so that dates in inserted documents
             * are formatted to UTC with the correct time zone offset
             */
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));
            super.onApplicationEvent(event);
        } else {
            logger.debug("MongoDB collection for class {} is populated. Not loading resources!",clazz.getSimpleName());
        }
    }
}
