/*
 * File: SequenceGenerator.java
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

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.tnsilver.contacts.model.Sequence;

@Service
public class SequenceGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SequenceGenerator.class);

    @Autowired
    private MongoOperations mongoOperations;

    public long generateSequence(String seqName) {

        Sequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)), new Update().inc("seq", 1),
            options().returnNew(true).upsert(true), Sequence.class);
        long seq = !Objects.isNull(counter) ? counter.getSeq() : 1;
        logger.trace("generated sequence id {} for collection {}", seq, seqName);
        return seq;
    }
}
