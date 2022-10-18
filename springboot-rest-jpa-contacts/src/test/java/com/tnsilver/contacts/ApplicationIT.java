/*
 * File: ApplicationIT.java
 * Creation Date: Jun 24, 2021
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
package com.tnsilver.contacts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.tnsilver.contacts.base.BaseJpaTest;

/**
 * Run this as a regular java program to execute curl commands from the console, without CORS or csrf protection
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApplicationIT extends BaseJpaTest {

    @LocalServerPort
    int port;
    @Value("${application.it.test.timeout}")
    int timeout;
    @Value("${application.it.test.timeunit}")
    TimeUnit unit;

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseJpaTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    /**
     * This test will run the application in test profile, for 10 minutes.
     * This allows the user to browse localhost:8080 and use the application.
     * @throws Exception - if anything goes wrong
     */
    @Test
    @DisplayName("application integration test")
    public void testApplication() throws Exception {
        // @formatter:off
        logger.debug("running application on port {} for {} {}", port, timeout, unit);
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<Boolean> future = null;
        CountDownLatch latch = new CountDownLatch(1);
        try {
            future = pool.submit(() -> Boolean.TRUE);
            latch.await(timeout, unit);
            assertTrue(future.get());
            logger.debug("exiting application after {} {}", timeout, unit);
        } catch (InterruptedException interrupt) {
            Thread.currentThread().interrupt();
            logger.warn("exiting application after because {}", interrupt.initCause(interrupt).getMessage());
        }
       // @formatter:on
    }

}
