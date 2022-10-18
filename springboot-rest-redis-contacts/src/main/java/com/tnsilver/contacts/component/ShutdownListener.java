/*
 * File: ShutdownListener.java
 * Creation Date: Jul 10, 2021
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import redis.embedded.RedisServer;

/**
 * The class ShutdownListener is responsible for shutting down the embedded Redis server
 * when the application context closes, so the next test case can start it. If the embedded
 * Redis server is not stopped between context swaps, an older PID may be listening on port
 * 6370 and the localhost:6370 address is already bound to another process.
 *
 * @author T.N.Silverman
 *
 */
@Profile({ "test", "dev" })
@Component
public class ShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownListener.class);
    // @formatter:off
    @Autowired private ApplicationContext context;
    @Autowired Environment env;
    // @formatter:on

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            /*
             *  We don't care about the exceptions this can cause...
             *  In fact, we just swallow them. However, they're
             *  caught here just for the sake of being aware.
             */
            logger.debug("Stopping embedded redis server...");
            LettuceConnectionFactory lettuceConnectionFactory = context.getBean(LettuceConnectionFactory.class);
            lettuceConnectionFactory.destroy();
            RedisServer redisServer = context.getBean(RedisServer.class);
            redisServer.stop();
        } catch (NoUniqueBeanDefinitionException nubdex) {
            /*
             *  There's more than one embedded RedisServer bean.
             *  This should never happen.
             */
        } catch (NoSuchBeanDefinitionException nsbdex) {
            /*
             *  There was never a redis server bean.
             *  This should never happen if we stick to
             *  profiles.
             */
        } catch (BeansException bex) {
            /*
             *  This may happen if tests are interrupted with
             *  CTRL+C so an older PID is listening on port 6370.
             *  For all intents and purposes this will never
             *  happen here.
             */
        }
    }
}