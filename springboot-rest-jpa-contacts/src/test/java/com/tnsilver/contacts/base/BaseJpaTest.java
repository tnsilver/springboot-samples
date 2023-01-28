/*
 * File: BaseJpaTest.java
 * Creation Date: Jul 20, 2021
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles({ "test" })
@Transactional
public abstract class BaseJpaTest extends BaseTest {

    @Autowired private Environment env;
    protected Logger log;

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseTest.beforeAll();
        String[] profiles = BaseJpaTest.class.getDeclaredAnnotation(ActiveProfiles.class).value();
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,
                           Arrays.stream(profiles).collect(Collectors.joining(",")));
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        log = LoggerFactory.getLogger(getClass());
        String profile = Stream.of(env.getActiveProfiles()).collect(Collectors.joining(","));
        log.debug("\n\nPROFILE: '{}', ENTERING '{}':\n", profile, info.getDisplayName());
    }
}
