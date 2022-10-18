/*
 * File: ContactRepositoryTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.keyvalue.core.IterableConverter;

import com.tnsilver.contacts.base.BaseRedisTest;
import com.tnsilver.contacts.base.PageableArgumentConverter;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.SocialSecurityNumber;

/**
 * The test ContactRepositoryIT tests the {@link ContactRepositoryImpl} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
public class ContactRepositoryIT extends BaseRedisTest {

    // @formatter:off
    @Autowired private ContactRepository repository;
    // @formatter:on

    private static final Pageable PAGEABLE = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseRedisTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @ParameterizedTest
    @DisplayName("test by example with ssn")
    // @formatter:off
    @CsvSource({ "678-21-4301",
                 "678-21-4302",
                 "678-21-4303",
                 "678-21-4304",
                 "678-21-4305" })
    // @formatter:on
    public void whenGetByExampleWithSsn_ThenFindExpecte(String _ssn) {
        var ssn = new SocialSecurityNumber(_ssn);
        var filter = new Contact(ssn, null, null, null, null, null);
        Page<Contact> actual = repository.getByExample(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
    }

    @ParameterizedTest
    @DisplayName("test by example with ssn and first name")
    // @formatter:off
    @CsvSource({ "678-21-4301,Homer",
                 "678-21-4302,Marge",
                 "678-21-4303,Lisa", "678-21-4304,Bart",
                 "678-21-4305,Maggie" })
    // @formatter:on
    public void whenGetByExampleWithFirstName_ThenFindExpecte(String _ssn, String firstName) {
        var ssn = new SocialSecurityNumber(_ssn);
        var filter = new Contact(ssn, firstName, null, null, null, null);
        Page<Contact> actual = repository.getByExample(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
    }

    @ParameterizedTest
    @DisplayName("test by example with ssn and names")
    // @formatter:off
    @CsvSource({ "678-21-4301,Homer,Simpson",
                 "678-21-4302,Marge,Simpson",
                 "678-21-4303,Lisa,Simpson",
                 "678-21-4304,Bart,Simpson",
                 "678-21-4305,Maggie,Simpson" })
    // @formatter:on
    public void whenGetByExampleWithSsnAndNames_ThenFindExpected(String _ssn, String firstName, String lastName) {
        var ssn = new SocialSecurityNumber(_ssn);
        var filter = new Contact(ssn, firstName, lastName, null, null, null);
        Page<Contact> actual = repository.getByExample(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
    }

    @ParameterizedTest
    @DisplayName("test by example without married or children")
    // @formatter:off
    @CsvSource({ "678-21-4301,Homer,Simpson,1978-06-20",
                 "678-21-4302,Marge,Simpson,1979-06-20",
                 "678-21-4303,Lisa,Simpson,1993-01-13",
                 "678-21-4304,Bart,Simpson,1995-09-18",
                 "678-21-4305,Maggie,Simpson,2003-03-05" })
    // @formatter:off
    public void whenGetByExampleWithBirthDate_ThenFindExpected(String _ssn,String firstName,String lastName,String birthDate) {
        var ssn = new SocialSecurityNumber(_ssn);
        var filter = new Contact(ssn, firstName, lastName,LocalDate.parse(birthDate), null, null);
        Page<Contact> actual = repository.getByExample(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
    }

    @Test
    @DisplayName("test by example without children")
    public void whenGetByExampleWithNoChildren_ThenFindExpected() {
        var ssn = new SocialSecurityNumber("678-21-4301");
        var filter = new Contact(ssn, "Homer", "Simpson", LocalDate.parse("1978-06-20"), true, null);
        var pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByExample(filter, pageable);
        assertFalse(actual.getContent().isEmpty());
    }

    @Test
    @DisplayName("test by complete example")
    public void whenGetByCompleteExample_ThenFinAll() {
        var ssn = new SocialSecurityNumber("678-21-4301");
        var filter = new Contact(ssn, "Homer", "Simpson", LocalDate.parse("1978-06-20"), true, 3);
        var pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByExample(filter, pageable);
        assertFalse(actual.getContent().isEmpty());
    }

    @Test
    @DisplayName("test by empty example")
    public void whenGetByEmptyExample_ThenFinAll() {
        var filter = new Contact();
        var pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByExample(filter, pageable);
        assertFalse(actual.getContent().isEmpty());
    }

    @ParameterizedTest
    @DisplayName("test get by params")
    // @formatter:off
     @CsvSource(value = { "678-21-4301,'','','','',''",
                          "'','','','','',3",
                          "'','','',1978-06-20,'',''",
                          "'','','',1978-06-20,true,3",
                          "'','',Simp,1978-06-20,true,3",
                          "'',Hom,Simp,1978-06-20,true,3",
                          "678-21-4301,H,S,1978-06-20,true,3",
                          "678-21-4301,H,'',1978-06-20,true,3" })
    // @formatter:on
    public void whenSupplyArbitraryParamsToGetByParams_ThenFindExpected(String _ssn, String firstName, String lastName,
        String _birthDate, String _married, String _children) {
        var ssn = null == _ssn || _ssn.isBlank() ? null : new SocialSecurityNumber(_ssn);
        var birthDate = null == _birthDate || _birthDate.isBlank() ? null : LocalDate.parse(_birthDate);
        var married = null == _married || _married.isBlank() ? null : Boolean.valueOf(_married);
        var children = null == _children || _children.isBlank() ? null : Integer.valueOf(_children);
        var filter = new Contact(ssn, firstName, lastName, birthDate, married, children);
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(filter.getSsn() == null ? null : filter.getSsn().getSsn(),
                                                      filter.getFirstName(), filter.getLastName(),
                                                      filter.getBirthDate(), filter.getMarried(), filter.getChildren(),
                                                      pageable);
        assertFalse(actual.getContent().isEmpty());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @DisplayName("test get by ssn")
    public void whenSupplySsnToGetByParams_ThenFindExpected() {
        var ssn = "678-21-4301";
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(ssn, null, null, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @Test
    @DisplayName("test get by first name")
    public void whenSupplyFirstNameToGetByParams_ThenFindExpected() {
        var firstName = "Hom";
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(null, firstName, null, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @Test
    @DisplayName("test get by ssn and first name")
    public void whenSupplySsnAndFirstNameToGetByParams_ThenFindExpected() {
        var ssn = "678-21-4301";
        var firstName = "Hom";
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(ssn, firstName, null, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @Test
    @DisplayName("test get by ssn and names")
    public void whenSupplySsnAndNamesGetByParams_ThenFindExpected() {
        var ssn = "678-21-4301";
        var firstName = "Hom";
        var lastName = "Simp";
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(ssn, firstName, lastName, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @Test
    @DisplayName("test get by all excluding birth date and children")
    public void whenOmitBirthDateAndChildrenFromGetByParams_ThenFindExpected() {
        var ssn = "678-21-4301";
        var firstName = "Hom";
        var lastName = "Simp";
        Boolean married = null;
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(ssn, firstName, lastName, null, married, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @Test
    @DisplayName("test get by all excluding birth date ")
    public void whenSupplyAllButBirthDateToGetByParams_ThenFindExpected() {
        var ssn = "678-21-4301";
        var firstName = "Hom";
        var lastName = "Simp";
        var married = true;
        var children = Integer.valueOf(3);
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(ssn, firstName, lastName, null, married, children, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @Test
    @DisplayName("test get by birth date ")
    public void whenSupplyBirthDateToGetByParams_ThenFindExpected() {
        var birthDate = LocalDate.parse("1978-06-20");
        var pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = repository.getByParams(null, null, null, birthDate, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        logger.trace("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> logger.trace("{}", c));
    }

    @ParameterizedTest
    @DisplayName("test find all pageable")
    @CsvSource(value = { "'page=0&size=10&sort=id,ASC','page=0&size=34&sort=id,ASC'" })
    void whenFindAllPageable_ThenPagingAsExpected(@ConvertWith(PageableArgumentConverter.class) Pageable pageable) {
        Page<Contact> actual = repository.findAll(pageable);
        assertEquals(pageable.getPageSize(), actual.getContent().size());
    }

    @Test
    @DisplayName("test find all unpaged")
    void whenFindAllUnpaged_ThenFindAll() {
        Page<Contact> actual = repository.findAll(Pageable.unpaged());
        assertEquals(NUM_OF_CONTACTS, actual.getContent().size());
    }

    @Test
    @DisplayName("test save all")
    public void whenSaveAllIterable_ThenAllSaved() {
        // @formatter:off
        List<Contact> contacts = List.of(
            new Contact(new SocialSecurityNumber("111-11-1111"),"test1","tester1",LocalDate.now(),true,1),
            new Contact(new SocialSecurityNumber("222-22-2222"),"test2","tester2",LocalDate.now(),false,0),
            new Contact(new SocialSecurityNumber("333-33-3333"),"test3","tester3",LocalDate.now(),true,3));
        // @formatter:on
        Iterable<Contact> results = repository.saveAll(contacts);
        results.forEach(actual -> assertNotNull(actual.getId()));
        results.forEach(repository::delete);
    }

    @Test
    @DisplayName("test update")
    public void whenUpdateContact_ThenUpdated() {
        var ssn = new SocialSecurityNumber("111-11-1111");
        var actual = new Contact(ssn, "test1", "tester1", LocalDate.now(), true, 1);
        actual = repository.save(actual);
        assertNotNull(actual.getId());
        var children = 3;
        var married = false;
        actual.setMarried(married);
        actual.setChildren(children);
        actual = repository.update(actual);
        assertNotNull(actual);
        assertEquals(married, actual.getMarried());
        assertEquals(children, actual.getChildren());
        repository.delete(actual);
    }

    @Test
    @DisplayName("test update")
    public void whenFindByIds_ThenAllFound() {
        final List<Long> ids = LongStream.rangeClosed(1, NUM_OF_CONTACTS).boxed().collect(Collectors.toList());
        repository.findAllById(ids).forEach(c -> {
            assertTrue(ids.contains(c.getId()));
        });
    }

    @ParameterizedTest
    @DisplayName("test find all sorted")
    @CsvSource(value = { "'page=0&size=10&sort=id,ASC'" })
    void whenFindAllSorted_ThenSorted(@ConvertWith(PageableArgumentConverter.class) Pageable pageable) {
        Iterable<Contact> results = repository.findAll(pageable.getSort());
        List<Contact> contacts = IterableConverter.toList(results);
        for (int i = 1; i < contacts.size(); i++) {
            Contact prev = contacts.get(i - 1);
            Contact next = contacts.get(i);
            assertTrue(next.getId() > prev.getId());
        }
    }

    @Test
    @DisplayName("test count")
    public void whenCount_ThenCountCorrect() {
        assertEquals(NUM_OF_CONTACTS, repository.count());
    }

    @Test
    @DisplayName("test exist by valid id")
    public void whenCallExistByIdWithValidId_ThenTrue() {
        assertTrue(repository.existsById(1L));
    }

    @Test
    @DisplayName("test exist by invalid id")
    public void whenCallExistByIdWithInvalidId_ThenFalse() {
        assertFalse(repository.existsById(-1L));
    }

    @Test
    @DisplayName("test delete all ids")
    public void whenDeleteAllByIdIterable_ThenAllDeleted() {
        // @formatter:off
        List<Contact> contacts = List.of(
            new Contact(new SocialSecurityNumber("111-11-1111"),"test1","tester1",LocalDate.now(),true,1),
            new Contact(new SocialSecurityNumber("222-22-2222"),"test2","tester2",LocalDate.now(),false,0),
            new Contact(new SocialSecurityNumber("333-33-3333"),"test3","tester3",LocalDate.now(),true,3));
        // @formatter:on
        Iterable<Contact> results = repository.saveAll(contacts);
        results.forEach(actual -> assertNotNull(actual.getId()));
        List<Long> ids = IterableConverter.toList(results).stream().map(Contact::getId).collect(Collectors.toList());
        repository.deleteAllById(ids);
        Iterable<Contact> actual = repository.findAllById(ids);
        assertFalse(actual.iterator().hasNext());
    }

    @Test
    @DisplayName("test delete all in iterable")
    public void whenDeleteAllIterable_ThenAllDeleted() {
        // @formatter:off
        List<Contact> contacts = List.of(
            new Contact(new SocialSecurityNumber("111-11-1111"),"test1","tester1",LocalDate.now(),true,1),
            new Contact(new SocialSecurityNumber("222-22-2222"),"test2","tester2",LocalDate.now(),false,0),
            new Contact(new SocialSecurityNumber("333-33-3333"),"test3","tester3",LocalDate.now(),true,3));
        // @formatter:on
        Iterable<Contact> results = repository.saveAll(contacts);
        List<Long> ids = IterableConverter.toList(results).stream().map(Contact::getId).collect(Collectors.toList());
        results.forEach(actual -> assertNotNull(actual.getId()));
        repository.deleteAll(results);
        Iterable<Contact> actual = repository.findAllById(ids);
        assertFalse(actual.iterator().hasNext());
    }

    @Test
    @DisplayName("test delete all")
    public void afterDeleteAll_ThenNoneFound() {
        repository.deleteAll();
        assertFalse(repository.findAll().iterator().hasNext());
        flushAndPopulate();
    }
}
