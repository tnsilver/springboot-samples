/*
 * File: BaseRedisTest.java
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
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "test" })
/*
 * this Dirties Context annotation must be here. It signals
 * spring to close the context after each test case and remove
 * it from the cache. Without it, the context will not close
 * after the test case, the embedded redis server shutdown
 * listener would not kick in, so the embedded server for the
 * next test case wouldn't be able to start. If the embedded
 * server is not stopped between test cases. the older test
 * case PID would remain listening on port 6370 so the address
 * localhost:6370 is already in use.
 */
@DirtiesContext
public abstract class BaseRedisTest extends BaseTest {

    // @formatter:off
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private ApplicationContext context;
    // @formatter:on

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseTest.beforeAll();
        String[] profiles = BaseRedisTest.class.getDeclaredAnnotation(ActiveProfiles.class).value();
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,
                           Arrays.stream(profiles).collect(Collectors.joining(",")));
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    protected void flushAndPopulate() {
        try (var conn = Objects.requireNonNull(redisTemplate.getConnectionFactory().getConnection(),
                                               "cannot aquire connection")) {
            conn.flushDb();
        }
        context.publishEvent(new ContextRefreshedEvent(context));
    }
}
