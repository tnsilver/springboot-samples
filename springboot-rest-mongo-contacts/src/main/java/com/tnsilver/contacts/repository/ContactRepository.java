/*
 * File: ContactRepository.java
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
package com.tnsilver.contacts.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.tnsilver.contacts.aspect.AuditableMethod;
import com.tnsilver.contacts.model.Contact;

@RepositoryRestResource(collectionResourceRel = "contacts", itemResourceRel = "contact")
public interface ContactRepository extends MongoRepository<Contact, Long> {

    @Override
    public Page<Contact> findAll(Pageable pageable);

    @SuppressWarnings("unchecked")
    @Override
    public Contact save(Contact entity);

    @AuditableMethod
    @Override
    public void delete(Contact entity);

    // @formatter:off
    @Query("{"
        + "'ssn.ssn': { '$regex': '?#{@qHelp.regex([0].ssn?.ssn)}' },"
        + "'firstName': { '$regex': '?#{@qHelp.regex([0].firstName)}' },"
        + "'lastName': { '$regex': '?#{@qHelp.regex([0].lastName)}' },"
        + "'birthDate': { '$gte': ?#{@qHelp.fromDate([0].birthDate)}, '$lt': ?#{@qHelp.toDate([0].birthDate)} },"
        + "'married': {'$in': ?#{@qHelp.booleans([0].married)} },"
        + "'children': {'$gte': ?#{@qHelp.ints([0].children,0)}, '$lte': ?#{@qHelp.ints([0].children,9999)} }"
        + "}")
    // @formatter:on
    @RestResource(exported = false, path = "byExample", rel = "byExample")
    public Page<Contact> getFilteredContacts(@Param(value = "example") Contact example, Pageable pageable);
    // @formatter:off

    @Query("{"
        + "'ssn.ssn': { '$regex': '?#{@qHelp.regex([0])}' },"
        + "'firstName': { '$regex': '?#{@qHelp.regex([1])}' },"
        + "'lastName': { '$regex': '?#{@qHelp.regex([2])}' },"
        + "'birthDate': { '$gte': ?#{@qHelp.fromDate([3])}, '$lt': ?#{@qHelp.toDate([3])} },"
        + "'married': {'$in': ?#{@qHelp.booleans([4])} },"
        + "'children': {'$gte': ?#{@qHelp.ints([5],0)}, '$lte': ?#{@qHelp.ints([5],9999)} }"
        + "}")
    // @formatter:on
    @RestResource(path = "byParams", rel = "byParams")
    public Page<Contact> getByParams(@Param(value = "ssn") String ssn,
                                     @Param("firstName") String firstName,
                                     @Param("lastName") String lastName,
                                     @Param("birthDate") /*@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)*/ LocalDate birthDate,
                                     @Param("married") Boolean married,
                                     @Param("children") Integer children, Pageable pageable);
    // @formatter:off
}