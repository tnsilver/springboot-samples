/*
 * File: RepositoryPopulatorTest.java
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
package com.tnsilver.contacts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import com.tnsilver.contacts.base.BaseRedisTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.repository.ContactRepository;

@SpringBootTest
// @Transactional
class RepositoryPopulatorTest extends BaseRedisTest {

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private RedisTemplate<?,?> redisTemplate;
    @Autowired
    private ApplicationContext context;

    @BeforeAll
    public static void beforeAll(TestInfo info) throws Exception {
        BaseRedisTest.beforeAll();
    }

    @BeforeEach
    public void flushRedis(TestInfo info) throws Exception {
        super.beforeEach(info);
        try (RedisConnection conn = Objects.requireNonNull(redisTemplate.getConnectionFactory().getConnection(), "cannot get redis connection")) {
            conn.flushAll();
        }
        // this triggers the repository populator
        context.publishEvent(new ContextRefreshedEvent(context));
    }

    @Test
    @DisplayName("test populator populates")
    public void whenContextLoadsAndCollectionEmpty_ThenPopulateCollection() {
        Iterable<Contact> results = contactRepository.findAll();
        List<Contact> contacts = IterableConverter.toList(results);
        assertEquals(NUM_OF_CONTACTS, contacts.size());
    }

}
