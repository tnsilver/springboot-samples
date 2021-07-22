/*
 * File: Sequence.java
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
package com.tnsilver.contacts.model;

import java.io.Serial;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Data;

/**
 * Class Sequence represents a sequential id generator hash, where the {@code id} is a {@code keyspace} of another hash
 * and the {@code seq} hashkey keeps track of the current value of the sequence. It is incremented when a new hash id
 * is generate.
 */
@Data
@RedisHash("sequence")
public class Sequence {

    @Serial
    private static final long serialVersionUID = -7494669495290071069L;
    @Id
    @Indexed
    private String id; // the id (hash name)
    private long seq; // the seqName
}