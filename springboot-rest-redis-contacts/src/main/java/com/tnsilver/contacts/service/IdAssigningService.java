/*
 * File: IdAssigningService.java
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
package com.tnsilver.contacts.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.tnsilver.contacts.model.BaseHash;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.Sequence;
import com.tnsilver.contacts.util.RedisUtils;

/**
 * The class IdAssigningService is responsible for assigning a sequential id property to a
 * {@link BaseHash} hash to replace the generated Redis UUID and arbitrary Long id's.
 *
 * @author T.N.Silverman
 *
 */
@Service
public class IdAssigningService {

    private static final Logger logger = LoggerFactory.getLogger(IdAssigningService.class);
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisMappingContext mappingContext;

    public IdAssigningService() {
        super();
    }

    /**
     * Assigns a sequential id to an {@link BaseHash} entity. The id originates in a sequence, which is
     * it self a hash. Each entity is suppose to have a designated hash with the field 'seq' storing the
     * the current value for the id. Each call to this method increments the value of the 'seq' field.
     * Sequences are identified by the key {@code 'sequence'} and the suffix, which is the entity hash key,
     * for example:  {@code 'sequence:contact'} stores the {@code 'seq'} value for the {@link Contact} entity.
     * The value is derived from the {@link RedisHash} type level annotation. If the annotation does not exist
     * on the entity, the value is derived by lower casing the entity class name.
     *
     * @param entity - the entity to assign an id to
     * @return - the newly assigned id.
     */
    // @formatter:off
    public <E extends BaseHash> Long assignId(E entity) {
        Assert.notNull(entity, "entity cannot be null!");
        if (null != entity.getId()) // if has id don't assign
            return entity.getId();
        String seqKeySpace = Optional.ofNullable(Sequence.class.getDeclaredAnnotation(RedisHash.class))
                                     .map(RedisHash::value)
                                     .orElse(Sequence.class.getSimpleName().toLowerCase()); // 'sequence'
        String entityKeySpace = RedisUtils.resolveKeySpace(mappingContext, entity.getClass());
        String keySpace = seqKeySpace + ":" + entityKeySpace; // 'sequence:contact'
        String seqHashKey = "seq";
        Long nextVal = 1L; // initial id
        boolean exist = RedisUtils.isSequenceExist(redisTemplate, keySpace, seqHashKey);
        if (exist) // increment and return the value of sequence:contact field 'seq'
            nextVal = redisTemplate.opsForHash().increment(keySpace, seqHashKey, 1);
        entity.setId(nextVal);
        logger.trace("assigned id '{}' from '{}' to {}", nextVal, keySpace, entity);
        return nextVal; // return for debug purpose only
    }
    // @formatter:on
}
