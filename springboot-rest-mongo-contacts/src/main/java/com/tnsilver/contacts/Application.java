/*
 * File: Application.java
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
package com.tnsilver.contacts;

import java.time.ZoneId;
import java.util.TimeZone;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import de.flapdoodle.embed.mongo.MongodExecutable;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired(required = false)
    private MongodExecutable mongodExecutable;

    /*
     * This is only required when running under IDE.
     * Our Maven plugins are configured to pass the -Duser.timezone=UTC
     * flag to the jvm.
     *
     * The flag is required because all mongo dates are stored in UTC
     * time zone.
     */
    public static final ZoneId defaultZoneId = ZoneId.of("Z");

    /**
     * shutdown the de.flapdoodle.embed.mongo (if in use) to avoid
     * address already in use errors in unit tests. This only happens
     * when unit tests load a new context before each test case.
     */
    @PreDestroy
    public void cleanup() {
        if (null != mongodExecutable) {
            try {
                logger.debug("closing embedded mongo");
                mongodExecutable.stop();
            } catch (Exception ex) {
                logger.warn("closing embedded mongo because {}", ex.initCause(ex).getMessage());
            }
        }
    }

    public static void main(String[] args) {
    	System.setProperty("os.arch", "x86_64");
        TimeZone.setDefault(TimeZone.getTimeZone(defaultZoneId));
        SpringApplication.run(Application.class, args);
    }
}
