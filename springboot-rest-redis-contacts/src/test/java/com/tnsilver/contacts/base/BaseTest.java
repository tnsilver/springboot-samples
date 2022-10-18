/*
 * File: BaseTest.java
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

import java.time.ZoneId;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

//@DirtiesContext
public class BaseTest {

    protected static final int NUM_OF_CONTACTS = 34;
    protected static final String DATABASE = "contacts";

    protected Logger logger;
    @Autowired
    private Environment env;

    @BeforeAll
    public static void beforeAll() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Z")));
    }

    @BeforeEach
    protected void beforeEach(TestInfo info) throws Exception {
        logger = LoggerFactory.getLogger(getClass());
        String profile = Stream.of(env.getActiveProfiles()).collect(Collectors.joining(","));
        logger.debug("\nPROFILE: '{}', ENTERING '{}':\n", profile, info.getDisplayName());
    }

}
