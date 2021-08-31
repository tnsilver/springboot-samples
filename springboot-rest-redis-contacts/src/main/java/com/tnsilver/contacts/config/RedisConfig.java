/*
 * File: JedisConfig.java
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
package com.tnsilver.contacts.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import com.tnsilver.contacts.component.ContactRepositoryPopulator;
import com.tnsilver.contacts.repository.ContactRepositoryImpl;

@Configuration
@EnableRedisRepositories(basePackageClasses = ContactRepositoryImpl.class)
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${redis.populator.location}")
    private String location;

    @PostConstruct
    public void init() {
        logger.debug("Redis configurations initialized");
    }

    @Bean
    public StringRedisTemplate redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();
        logger.trace("StringRedisTemplate Bean Initialized");
        return template;
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean respositoryPopulator(ContactRepositoryPopulator populator) {
        populator.setResources(new Resource[] { new ClassPathResource(location) });
        logger.trace("Jackson2RepositoryPopulatorFactoryBean Bean Initialized");
        return populator;
    }

    @Bean
    public HashMapper<Object, byte[], byte[]> hashMapper() {
        return new ObjectHashMapper();
    }
}