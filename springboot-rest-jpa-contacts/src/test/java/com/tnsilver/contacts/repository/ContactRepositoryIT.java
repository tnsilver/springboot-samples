/*
 * File: ContactRepositoryIT.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

import com.tnsilver.contacts.base.BaseJpaTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.SocialSecurityNumber;

import lombok.extern.slf4j.Slf4j;

/**
 * The test JpaContactRepositoryIT tests the {@link ContactRepository} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
@Transactional
@Slf4j
public class ContactRepositoryIT extends BaseJpaTest {

    @Autowired private ContactRepository contactRepository;
    private static final Pageable PAGEABLE = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseJpaTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @ParameterizedTest
    @DisplayName("test filter contacts by ssn")
    // @formatter:off
    @CsvSource({ "123-45-6789,Homer",
                 "123-45-7890,Marge",
                 "123-45-8901,Lisa",
                 "123-45-9012,Bart",
                 "123-45-1012,Maggie" })
    // @formatter:on
    public void whenGetFilterBySsn_ThenFindExpected(String ssn, String expected) {
        Contact filter = new Contact(new SocialSecurityNumber(ssn), null, null, null, null, null);
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        assertEquals(expected, actual.getContent().get(0).getFirstName());
    }

    @ParameterizedTest
    @DisplayName("test filtered contacts by partial first name")
    // @formatter:off
    @CsvSource({ "123-45-6789,H",
                 "123-45-7890,Marge",
                 "123-45-8901,Lis",
                 "123-45-9012,Ba",
                 "123-45-1012,Maggi" })
    // @formatter:on
    public void whenFilterByFirstName_ThenFindExpected(String ssn, String firstName) {
        Contact filter = new Contact(new SocialSecurityNumber(ssn), firstName, null, null, null, null);
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        assertTrue(actual.getContent().get(0).getFirstName().startsWith(firstName));
    }

    @ParameterizedTest
    @DisplayName("test filter contacts by names")
    // @formatter:off
    @CsvSource({ "123-45-6789,H,Simp",
                 "123-45-7890,Marge,Si",
                 "123-45-8901,Lis,Simpson",
                 "123-45-9012,Ba,Sim",
                 "123-45-1012,Maggie,S" })
    // @formatter:on
    public void whenFilterByNames_ThenFindExpected(String ssn, String firstName, String lastName) {
        Contact filter = new Contact(new SocialSecurityNumber(ssn), firstName, lastName, null, null, null);
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        assertTrue(actual.getContent().get(0).getFirstName().startsWith(firstName));
        assertTrue(actual.getContent().get(0).getLastName().startsWith(lastName));
    }

    @ParameterizedTest
    @DisplayName("test filter by birth date")
    // @formatter:off
    @CsvSource({ "123-45-6789,H,Simp,1978-06-20",
                 "123-45-7890,Marge,Si,1979-06-20",
                 "123-45-8901,Lis,Simpson,1993-01-13",
                 "123-45-9012,Ba,Sim,1995-09-18",
                 "123-45-1012,Maggie,S,2003-03-05" })
    // @formatter:off
    public void whenFilterByBirthDate_ThenFindExpected(String ssn,String firstName,String lastName,String birthDate) {
        Contact filter = new Contact(new SocialSecurityNumber(ssn),firstName, lastName,LocalDate.parse(birthDate), null, null);
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, PAGEABLE);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        assertTrue(actual.getContent().get(0).getFirstName().startsWith(firstName));
        assertTrue(actual.getContent().get(0).getLastName().startsWith(lastName));
        assertTrue(actual.getContent().get(0).getBirthDate().equals(filter.getBirthDate()));
    }

    @Test
    @DisplayName("test filter by married")
    public void whenFilterByMarried_ThenFindExpected() {
        Contact filter = new Contact(new SocialSecurityNumber("123-45-6789"), "Home", "Simp", LocalDate.parse("1978-06-20"), true, null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertTrue(actual.getContent().get(0).getFirstName().startsWith(filter.getFirstName()));
        assertTrue(actual.getContent().get(0).getLastName().startsWith(filter.getLastName()));
        assertTrue(actual.getContent().get(0).getBirthDate().equals(filter.getBirthDate()));
    }

    @Test
    @DisplayName("test filter by last name and married")
    public void whenFilterByLastNameAndMarried_ThenFindExpected() {
        Contact filter = new Contact(null, "", "Simp", null, true, null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, pageable);
        assertFalse(actual.getContent().isEmpty());
        // homer and marge (married)
        assertEquals(2, actual.getContent().size());
        actual.getContent().forEach(c -> assertTrue(c.getLastName().startsWith("Simp")));
    }


    @Test
    @DisplayName("test filter by children")
    public void whenFilterByChildren_ThenFindExpected() {
        Contact filter = new Contact(new SocialSecurityNumber("123-45-6789"), "Home", "Simp", LocalDate.parse("1978-06-20"), true, 3);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertTrue(actual.getContent().get(0).getFirstName().startsWith(filter.getFirstName()));
        assertTrue(actual.getContent().get(0).getLastName().startsWith(filter.getLastName()));
        assertTrue(actual.getContent().get(0).getBirthDate().equals(filter.getBirthDate()));
        assertTrue(actual.getContent().get(0).getMarried().equals(filter.getMarried()));
        assertTrue(actual.getContent().get(0).getChildren().equals(filter.getChildren()));
    }

    @Test
    @DisplayName("test filter by nothing")
    public void whenFilterNothing_ThenFindAll() {
        Contact filter = new Contact();
        filter.setId(null);
        filter.setSsn(null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getFilteredContacts(filter, pageable);
        assertFalse(actual.getContent().isEmpty());
    }

    @ParameterizedTest
    @DisplayName("test get by params")
    // @Disabled
    // @formatter:off
     @CsvSource(value = { "123-45-6789,'','','','',''",
                          "'','','','','',3",
                          "'','','',1978-06-20,'',''",
                          "'','','',1978-06-20,true,3",
                          "'','',Simp,1978-06-20,true,3",
                          "'',Hom,Simp,1978-06-20,true,3",
                          "123-45-6789,H,S,1978-06-20,true,3",
                          "123-45-6789,H,'',1978-06-20,true,3" })
    // @formatter:on
    public void whenGetByParams_ThenFound(String _ssn, String firstName, String lastName, String _birthDate,
        String _married, String _children) {
        SocialSecurityNumber ssn = null == _ssn || _ssn.isBlank() ? null : new SocialSecurityNumber(_ssn);
        LocalDate birthDate = null == _birthDate || _birthDate.isBlank() ? null : LocalDate.parse(_birthDate);
        Boolean married = null == _married || _married.isBlank() ? null : Boolean.valueOf(_married);
        Integer children = null == _children || _children.isBlank() ? null : Integer.valueOf(_children);
        Contact filter = new Contact(ssn, firstName, lastName, birthDate, married, children);
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(filter.getSsn() == null ? null : filter.getSsn().getSsn(),
                                                             filter.getFirstName(), filter.getLastName(),
                                                             filter.getBirthDate(), filter.getMarried(),
                                                             filter.getChildren(), pageable);
        assertFalse(actual.getContent().isEmpty());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }

    @Test
    @DisplayName("test get by ssn param")
    public void whenGetBySsnParam_ThenFound() {
        String ssn = "123-45-6789";
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(ssn, null, null, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }

    @Test
    @DisplayName("test get by first name param")
    public void whenGetByFirstNameParam_ThenFound() {
        String firstName = "Hom";
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(null, firstName, null, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }

    @Test
    @DisplayName("test get by names params")
    public void whenGetByNamesParams_ThenFound() {
        String ssn = "123-45-6789";
        String firstName = "Hom";
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(ssn, firstName, null, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }

    @Test
    @DisplayName("test get by ssn and names params")
    public void whenGetBySsnAndNamesParams_ThenFound() {
        String ssn = "123-45-6789";
        String firstName = "Hom";
        String lastName = "Simp";
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(ssn, firstName, lastName, null, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }

    @Test
    @DisplayName("test get by ssn, names, married params")
    public void whenGetBySsnNamesAndMarriedParams_ThenFound() {
        String ssn = "123-45-6789";
        String firstName = "Hom";
        String lastName = "Simp";
        Boolean married = null;
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(ssn, firstName, lastName, null, married, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }

    @Test
    @DisplayName("test get by ssn, names, married, children params")
    public void whenGetBySsnNamesMarriedAndChildrenParams_ThenFound() {
        String ssn = "123-45-6789";
        String firstName = "Hom";
        String lastName = "Simp";
        Boolean married = true;
        Integer children = Integer.valueOf(3);
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(ssn, firstName, lastName, null, married, children,
                                                             pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }

    @Test
    @DisplayName("test get by birthdate param")
    public void whenGetByBirthdateParam_ThenFound() {
        LocalDate birthDate = LocalDate.parse("1978-06-20");
        PageRequest pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"));
        Page<Contact> actual = contactRepository.getByParams(null, null, null, birthDate, null, null, pageable);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
        System.out.println();
        log.debug("{} results:", actual.getContent().size());
        actual.getContent().forEach(c -> log.debug("{}", c));
        System.out.println();
    }
}
