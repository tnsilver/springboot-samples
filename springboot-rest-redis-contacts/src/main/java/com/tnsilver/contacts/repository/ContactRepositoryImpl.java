/*
 * File: ContactRepositoryImpl.java
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
import static com.tnsilver.contacts.util.RedisUtils.isMatchingContact;
import static com.tnsilver.contacts.util.RedisUtils.isTypeMatch;
import static com.tnsilver.contacts.util.RedisUtils.resolveKeySpace;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.data.keyvalue.core.KeyValueCallback;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.repository.support.QueryByExampleRedisExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;

import com.tnsilver.contacts.annotation.AuditBeforeDelete;
import com.tnsilver.contacts.annotation.AuditBeforeModification;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.ContactRecord;
import com.tnsilver.contacts.service.IdAssigningService;
import com.tnsilver.contacts.util.RedisUtils;

/**
 * Class ContactRepositoryImpl is custom implementation of a {@link Contact} making it possible to query
 * Redis for hashes meeting specific search criteria, as is Spring Boot Data JPA repositories.
 *
 * @author T.N.Silverman
 *
 */
public class ContactRepositoryImpl implements PagingAndSortingRepository<Contact, Long>, ContactRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(ContactRepositoryImpl.class);
    // @formatter:off
    @Autowired private RedisKeyValueTemplate keyValueTemplate;
    @Autowired private IdAssigningService idAssigner;
    @Autowired private Repositories repositories;
    @Autowired private RedisMappingContext mappingContext;
    // @formatter:on

    private QueryByExampleRedisExecutor<Contact> executor;



    @PostConstruct
    public void init() {
        EntityInformation<Contact, Long> entityInformation = repositories.getEntityInformationFor(Contact.class);
        executor = new QueryByExampleRedisExecutor<>(entityInformation, keyValueTemplate);
        logger.trace("POST CONSTRUCTED");
    }

    // -------------------------------------------------------------------------
    // Custom Methods
    // -------------------------------------------------------------------------

    // @formatter:off
    public Page<Contact> getByExample(Contact probe, Pageable pageable) {
        Example<Contact> example = Example.of(probe,ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(StringMatcher.DEFAULT));
        Page<Contact> page = executor.findAll(example, pageable);
        logger.trace("found {} contacts matching {}", page.getContent().size(), example.getProbe());
        return page;
    }
    // @formatter:on

    // @formatter:off
    @Override
    public Page<Contact> getByParams(String ssn, String firstName, String lastName, LocalDate birthDate, Boolean married, Integer children, Pageable pageable) {
        ContactRecord example = new ContactRecord(ssn, firstName, lastName, birthDate, married, children);
        long total = RedisUtils.countMatchingContacts(ssn, firstName, lastName, birthDate, married, children, pageable,mappingContext,keyValueTemplate);
        final Class<Contact> type = Contact.class;
        KeyValueQuery<?> query = new KeyValueQuery<>(pageable.getSort());
        String keyspace = resolveKeySpace(mappingContext, type);
        KeyValueCallback<Iterable<Contact>> action = adapter -> {
            Iterable<Contact> result = adapter.find(query, keyspace, type);
            AtomicLong counter = new AtomicLong();
            final long start = pageable.getOffset();
            final long end = start + pageable.getPageSize();
            return IterableConverter.toList(result)
                                    .stream().filter(obj -> isTypeMatch(type, obj))
                                             .map(obj -> type.cast(obj))
                                             .filter(contact -> {
                                                 if (isMatchingContact(contact, example)) {
                                                     long count = counter.get();
                                                     counter.incrementAndGet();
                                                     return count < end && count >= start;
                                                 } else return false;
                                              }).collect(toList());
        };
        Iterable<Contact> content = RedisUtils.executeRequired(keyValueTemplate, action);
        List<Contact> contacts = IterableConverter.toList(content);
        return new PageImpl<>(contacts, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()), total);
    }
    // @formatter:on

    // -------------------------------------------------------------------------
    // Methods from CrudRepository
    // -------------------------------------------------------------------------

    @Override
    @AuditBeforeModification
    public <S extends Contact> S save(S contact) {
        Assert.notNull(contact, "contact must not be null!");
        idAssigner.assignId(contact);
        contact = keyValueTemplate.insert(contact);
        logger.trace("saved {}", contact);
        return contact;
    }

    @SuppressWarnings("unchecked")
    @Override
    @AuditBeforeModification
    public <S extends Contact> S update(S entity) {
        Assert.notNull(entity, "entity must not be null!");
        Long id = entity.getId();
        Optional<Contact> candidate = findById(id);
        Assert.state(candidate.isPresent(), String.format("cannot update b/c no contact with id %s exists!", id));
        Contact contact = candidate.get();
        copyProperties(entity, contact, "createdBy", "createdOn");
        entity = keyValueTemplate.update(id, (S) contact);
        logger.trace("updated {}", entity);
        return entity;
    }

    @Override
    @AuditBeforeModification
    public <S extends Contact> Iterable<S> saveAll(Iterable<S> contacts) {
        Assert.notNull(contacts, "The given iterable of audits must not be null!");
        List<S> list = IterableConverter.toList(contacts);
        logger.trace("saving {} audits", list.size());
        return list.stream().map(this::save).collect(toList());
    }

    @Override
    public Optional<Contact> findById(Long id) {
        Assert.notNull(id, "The given id must not be null!");
        logger.trace("finding by id {}", id);
        return keyValueTemplate.findById(id, Contact.class);
    }

    @Override
    public boolean existsById(Long id) {
        logger.trace("checking if id {} exists", id);
        return findById(id).isPresent();
    }

    @Override
    public List<Contact> findAll() {
        logger.trace("finding all contacts");
        return IterableConverter.toList(keyValueTemplate.findAll(Contact.class));
    }

    @Override
    public Iterable<Contact> findAllById(Iterable<Long> ids) {
        Assert.notNull(ids, "The given iterable of id's must not be null!");
        List<Long> list = IterableConverter.toList(ids);
        logger.trace("finding {} ids", list.size());
        return list.stream().map(this::findById).map(o -> o.orElse(null)).filter(Objects::nonNull).collect(toList());
    }

    @Override
    public long count() {
        logger.trace("counting");
        return keyValueTemplate.count(Contact.class);
    }

    @Override
    @AuditBeforeDelete
    public void deleteById(Long id) {
        Assert.notNull(id, "The given id must not be null!");
        logger.trace("deleteing id {}", id);
        keyValueTemplate.delete(id, Contact.class);
    }

    @Override
    @AuditBeforeDelete
    public void delete(Contact contact) {
        Assert.notNull(contact, "The given contact must not be null!");
        logger.trace("deleting {}", contact);
        deleteById(contact.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        Assert.notNull(ids, "The given iterable of ids must not be null!");
        logger.trace("deleting all ids");
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Contact> contacts) {
        Assert.notNull(contacts, "The given iterable of contacts must not be null!");
        logger.trace("deleting contacts");
        contacts.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        logger.trace("deleting all contacts");
        keyValueTemplate.delete(Contact.class);
    }

    // -------------------------------------------------------------------------
    // Methods from PagingAndSortingRepository
    // -------------------------------------------------------------------------

    @Override
    public Iterable<Contact> findAll(Sort sort) {
        Assert.notNull(sort, "sort must not be null!");
        logger.trace("finding all contacts sorted by {}", sort);
        return keyValueTemplate.findAll(sort, Contact.class);
    }

    @Override
    public Page<Contact> findAll(Pageable pageable) {
        Assert.notNull(pageable, "pageable must not be null!");
        logger.trace("finding all contacts and paging by {}", pageable);
        if (pageable.isUnpaged()) {
            logger.trace("finding all contacts b/c no paging data was found");
            List<Contact> result = findAll();
            return new PageImpl<>(result, Pageable.unpaged(), result.size());
        }
        Iterable<Contact> content = keyValueTemplate.findInRange(pageable.getOffset(), pageable.getPageSize(),
                                                                 pageable.getSort(), Contact.class);
        return new PageImpl<>(IterableConverter.toList(content), pageable, this.keyValueTemplate.count(Contact.class));
    }
}