/*
 * File: ContactRepository.java
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
package com.tnsilver.contacts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.tnsilver.contacts.aspect.AuditableMethod;
import com.tnsilver.contacts.model.Contact;

@RepositoryRestResource(collectionResourceRel = "contacts", itemResourceRel = "contact")
public interface ContactRepository extends CrudRepository<Contact, Long>, PagingAndSortingRepository<Contact, Long> {

    @Override
    public Page<Contact> findAll(Pageable pageable);

    @Override
    public List<Contact> findAll();

    @SuppressWarnings("unchecked")
    @Override
    public Contact save(Contact entity);

    /**
     * This is only here for AOP aspect auditing purpose
     */
    @AuditableMethod
    //@PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Override
    public void delete(Contact entity);

    // @formatter:off
    @RestResource(exported = false)
    @Query("SELECT c from Contact c WHERE "
        + "CASE WHEN c.ssn is not null THEN c.ssn.ssn ELSE c.ssn END "
        + "LIKE CONCAT(COALESCE(:#{#example.ssn?.ssn},''),'%') "
        + "AND c.firstName LIKE CONCAT(COALESCE(:#{#example.firstName},''),'%') "
        + "AND c.lastName LIKE CONCAT(COALESCE(:#{#example.lastName},''),'%') "
        + "AND c.birthDate = COALESCE(:#{#example.birthDate},c.birthDate) "
        + "AND c.married = COALESCE(:#{#example.married}, c.married) "
        + "AND COALESCE(c.children,0) = COALESCE(:#{#example.children},COALESCE(c.children,0)) ")
    /*@Query("SELECT c from Contact c WHERE "
        + "CASE WHEN c.ssn is not null THEN c.ssn.ssn ELSE c.ssn END "
        + "LIKE CONCAT(COALESCE(:#{#example.ssn?.ssn},''),'%') "
        + "AND c.firstName LIKE CONCAT(COALESCE(:#{#example.firstName},''),'%') "
        + "AND c.lastName LIKE CONCAT(COALESCE(:#{#example.lastName},''),'%') "
        + "AND c.birthDate = COALESCE(:#{#example.birthDate},c.birthDate) "
        + "AND COALESCE(c.married,FALSE) = COALESCE(:#{#example.married != null ? (!#example.married ? NULL : #example.married) : NULL},COALESCE(c.married,FALSE)) "
        + "AND COALESCE(c.children,0) = COALESCE(:#{#example.children},COALESCE(c.children,0)) ")*/
    public Page<Contact> getFilteredContacts(@Param(value = "example") Contact example,
                                             Pageable pageable);
    // @formatter:on

    @RestResource(path = "byParams", rel = "byParams")
    // @formatter:off
    @Query("SELECT c from Contact c WHERE "
        + "CASE WHEN c.ssn is not null THEN c.ssn.ssn ELSE c.ssn END "
        + "LIKE CONCAT(COALESCE(:ssn,''),'%') " + "AND c.firstName LIKE CONCAT(COALESCE(:firstName,''),'%') "
        + "AND c.lastName LIKE CONCAT(COALESCE(:lastName,''),'%') " + "AND c.birthDate = COALESCE(:birthDate,c.birthDate) "
        + "AND c.married = COALESCE(:married, c.married)"
        + "AND COALESCE(c.children,0) = COALESCE(:children,COALESCE(c.children,0)) ")
    public Page<Contact> getByParams(@Param(value = "ssn") String ssn,
                                     @Param("firstName") String firstName,
                                     @Param("lastName") String lastName,
                                     @Param("birthDate") LocalDate birthDate,
                                     @Param("married") Boolean married,
                                     @Param("children") Integer children, Pageable pageable);
    // @formatter:on
}