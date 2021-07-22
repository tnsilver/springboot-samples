/*
 * /*
 * File: ContactRepositoryPopulator.java
 * Creation Date: Jul 8, 2021
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
package com.tnsilver.contacts.component;

import java.time.ZoneId;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.stereotype.Component;

import com.tnsilver.contacts.model.BaseHash;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.util.RedisUtils;

/**
 * This extension of Jackson2RepositoryPopulatorFactoryBean reacts to {@code ContextRefreshedEvent} and
 * if when no redis hashes are found under the keyspace corresponding to the @RedisHash annotation {@code value}
 * this factory will load all the hashes in the {@code data.json} file to the redis database.
 *
 * @author T.N.Silverman
 *
 */
@Component
public class ContactRepositoryPopulator extends Jackson2RepositoryPopulatorFactoryBean {

    private final static Logger logger = LoggerFactory.getLogger(ContactRepositoryPopulator.class);

    // @formatter:off
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private RedisMappingContext mappingContext;
    // @formatter:on

    @Value("${redis.populator.location}")
    private String location;
    private String keyspace;
    private Class<? extends BaseHash> clazz;


    public ContactRepositoryPopulator() {
        this(Contact.class);
    }

    private ContactRepositoryPopulator(Class<? extends BaseHash> clazz) {
        super();
        this.clazz = clazz;
    }

    @PostConstruct
    public void init() {
        this.keyspace = RedisUtils.resolveKeySpace(mappingContext, clazz);;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        long count = 0;
        try (RedisConnection conn = redisTemplate.getConnectionFactory().getConnection()) {
            count = conn.sCard(keyspace.getBytes());
        }
        // long count = (null == redisTemplate) ? repository.count() : redisTemplate.keys(keyspace).size();
        if (count == 0) {
            String path = new ClassPathResource(location).getPath();
            logger.debug("loading data from {}", path);
            /*
             * set the default time zone to the system default, so that dates in inserted documents
             * are formatted to UTC with the correct time zone offset
             */
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));
            super.onApplicationEvent(event);
        } else {
            logger.debug("Found {} redis keys for keyspace '{}'", count, keyspace);
        }
    }
}
