/*
 * File: BaseEntity.java
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
package com.tnsilver.contacts.model;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface BaseEntity is a serializeable base for any persstence entity
 *
 * @author T.N.Silverman
 */
public interface BaseEntity extends Serializable {

    static final Logger logger = LoggerFactory.getLogger(BaseEntity.class);
    static final Map<Class<? extends BaseEntity>, WeakReference<String>> cache = new HashMap<>();
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static final ReadLock readLock = lock.readLock();
    static final WriteLock writeLock = lock.writeLock();

    public Long getId();

    public void setId(Long id);

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);

    @Override
    public String toString();
}
