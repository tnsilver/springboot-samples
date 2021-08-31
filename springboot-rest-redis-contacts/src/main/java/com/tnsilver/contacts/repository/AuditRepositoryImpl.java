/*
 * File: AuditRepositoryImpl.java
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
package com.tnsilver.contacts.repository;

import static com.tnsilver.contacts.util.RedisUtils.copyProperties;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.Assert;

import com.tnsilver.contacts.annotation.AuditBeforeDelete;
import com.tnsilver.contacts.annotation.AuditBeforeModification;
import com.tnsilver.contacts.model.Audit;
import com.tnsilver.contacts.service.IdAssigningService;
/**
 * Class ContactRepositoryImpl is custom implementation of a {@link Audit} making it possible to query
 * Redis for hashes meeting specific search criteria, as is Spring Boot Data JPA repositories.
 *
 * @author T.N.Silverman
 *
 */
public class AuditRepositoryImpl implements CrudRepository<Audit, Long>, AuditRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(AuditRepositoryImpl.class);
    @Autowired
    private RedisKeyValueTemplate keyValueTemplate;
    @Autowired
    private IdAssigningService idAssigner;

    // -------------------------------------------------------------------------
    // Methods from CrudRepository
    // -------------------------------------------------------------------------

    @Override
    @AuditBeforeModification
    public <S extends Audit> S save(S audit) {
        Assert.notNull(audit, "Audit must not be null!");
        idAssigner.assignId(audit);
        audit = keyValueTemplate.insert(audit);
        logger.trace("saved {}", audit);
        return audit;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S extends Audit> S update(S entity) {
        Assert.notNull(entity, "Audit must not be null!");
        Long id = entity.getId();
        Optional<Audit> candidate = findById(id);
        Assert.state(candidate.isPresent(), String.format("cannot update b/c no contact with id %s exists!", id));
        Audit audit = candidate.get();
        copyProperties(entity, audit, "createdBy", "createdOn");
        entity = keyValueTemplate.update(id, (S) audit);
        logger.trace("updated {}", entity);
        return entity;
    }

    @Override
    public <S extends Audit> Iterable<S> saveAll(Iterable<S> audits) {
        Assert.notNull(audits, "The given iterable of audits must not be null!");
        List<S> list = IterableConverter.toList(audits);
        logger.trace("saving {} audits", list.size());
        return list.stream().map(this::save).collect(toList());
    }

    @Override
    public Optional<Audit> findById(Long id) {
        Assert.notNull(id, "The given id must not be null!");
        logger.trace("finding by id {}", id);
        return keyValueTemplate.findById(id, Audit.class);
    }

    @Override
    public boolean existsById(Long id) {
        logger.trace("checking if id {} exists", id);
        return findById(id).isPresent();
    }

    @Override
    public List<Audit> findAll() {
        logger.trace("finding all");
        return IterableConverter.toList(keyValueTemplate.findAll(Audit.class));
    }

    @Override
    public Iterable<Audit> findAllById(Iterable<Long> ids) {
        Assert.notNull(ids, "The given iterable of id's must not be null!");
        List<Long> list = IterableConverter.toList(ids);
        logger.trace("finding {} ids", list.size());
        return list.stream().map(this::findById).map(o -> o.orElse(null)).filter(Objects::nonNull).collect(toList());
    }

    @Override
    public long count() {
        logger.trace("counting");
        return keyValueTemplate.count(Audit.class);
    }

    @Override
    @AuditBeforeDelete
    public void deleteById(Long id) {
        Assert.notNull(id, "The given id must not be null!");
        logger.trace("deleteing id {}", id);
        keyValueTemplate.delete(id, Audit.class);
    }

    @Override
    public void delete(Audit audit) {
        Assert.notNull(audit, "The given audit must not be null!");
        logger.trace("deleting {}", audit);
        deleteById(audit.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        Assert.notNull(ids, "The given iterable of ids must not be null!");
        logger.trace("deleting all ids");
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Audit> audits) {
        Assert.notNull(audits, "The given Iterable of entities must not be null!");
        logger.trace("deleting entities");
        audits.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        logger.trace("deleting all audits");
        keyValueTemplate.delete(Audit.class);
    }
}