/*
 * File: DomainObjectsTest.java
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tnsilver.contacts.base.BaseJpaTest;

/**
 * The test DomainObjectsTest tests the application domain objects {@link Contact}, {@link SocialSecurityNumber},
 * {@link Audit} and {@link Error}
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DomainObjectsTest extends BaseJpaTest {

    @Resource
    private Environment env;

    @ParameterizedTest
    @DisplayName("test contact getters")
    @CsvSource("1,Test,Contact,123-45-6789,1980-08-31,true,2")
    public void whenCreateContact_ThenAllGettersReturnExpectedValues(Long id, String firstName, String lastName,
        SocialSecurityNumber ssn, LocalDate birthDate, Boolean married, Integer children) throws Exception {
        Contact actual = new Contact(ssn, firstName, lastName, birthDate, married, children);
        actual.setId(id);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
            () -> assertEquals(id, actual.getId()),
            () -> assertEquals(firstName, actual.getFirstName()),
            () -> assertEquals(lastName, actual.getLastName()),
            () -> assertEquals(ssn, actual.getSsn()),
            () -> assertEquals(birthDate, actual.getBirthDate()),
            () -> assertEquals(married, actual.getMarried()),
            () -> assertEquals(children, actual.getChildren()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test contact setters")
    @CsvSource("1,Test,Contact,123-45-6789,1980-08-31,true,2")
    public void whenCreateContact_ThenAllSettersSetExpectedValues(Long id, String firstName, String lastName, SocialSecurityNumber ssn,
        LocalDate birthDate, Boolean married, Integer children) throws Exception {
        Contact actual = new Contact();
        actual.setId(id);
        actual.setFirstName(firstName);
        actual.setLastName(lastName);
        actual.setSsn(ssn);
        actual.setBirthDate(birthDate);
        actual.setMarried(married);
        actual.setChildren(children);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
            () -> assertEquals(id, actual.getId()),
            () -> assertEquals(firstName, actual.getFirstName()),
            () -> assertEquals(lastName, actual.getLastName()),
            () -> assertEquals(ssn, actual.getSsn()),
            () -> assertEquals(birthDate, actual.getBirthDate()),
            () -> assertEquals(married, actual.getMarried()),
            () -> assertEquals(children, actual.getChildren()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test contact hashcode and equals")
    @CsvSource("1,Test,Contact,123-45-6789,1980-08-31,true,2")
    public void whenCreateContact_ThenHashCodeFunctionIsOk(Long id, String firstName, String lastName, SocialSecurityNumber ssn,
        LocalDate birthDate, Boolean married, Integer children) throws Exception {
        Contact actual = new Contact(ssn, firstName, lastName, birthDate, married, children);
        actual.setId(id);
        Contact expected = new Contact(ssn, firstName, lastName, birthDate, married, children);
        expected.setId(id);
        expected.setCreatedOn(actual.getCreatedOn());
        expected.setModifiedOn(actual.getModifiedOn());
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
                () -> assertNotNull(expected),
                () -> assertEquals(expected, actual),
                () -> assertNotNull(expected.hashCode()),
                () -> assertEquals(expected.toString(), actual.toString()),
                () -> assertEquals(expected.hashCode(), actual.hashCode()),
                () -> assertFalse(expected.equals(null)));
        // @formatter:on
    }

    @Test
    @DisplayName("test noargs constructor contact hashcode")
    public void whenCreateDefaultContact_ThenHashCodeFunctionIsStillOk() throws Exception {
        assertEquals(new Contact().hashCode(), new Contact().hashCode());
    }


    @Test
    @DisplayName("test contact equals")
    public void whenCreateContact_ThenEqualsOk() throws Exception {
        Contact actual = new Contact(new SocialSecurityNumber("111-11-1111"), "Test", "Tester", LocalDate.now(), false, 0);
        Contact other = new Contact(new SocialSecurityNumber("222-22-2222"), "Tset", "RetSet", LocalDate.now(), true, 1);
        // @formatter:off
        assertTrue(new Contact().equals(new Contact()));
        assertFalse(actual.equals(null));
        assertFalse(new Contact().equals(null));
        assertFalse(new Contact().equals(new Object()));
        assertFalse(actual.equals(other));
        assertTrue(actual.equals(actual));
        // @formatter:on
    }

    @Test
    @DisplayName("test audit equals")
    public void whenCreateAudit_ThenEqualsOk() throws Exception {
        Audit actual = new Audit(1L,"test","test","test");
        Audit other = new Audit(2L,"tset","tset","tset");
        // @formatter:off
        assertTrue(new Audit().equals(new Audit()));
        assertTrue(actual.equals(actual));
        assertFalse(actual.equals(null));
        assertFalse(new Audit().equals(null));
        assertFalse(actual.equals(other));
        assertFalse(new Audit().equals(new Object()));
        // @formatter:on
    }


    @Test
    @DisplayName("test error equals")
    public void whenCreateError_ThenEqualsOk() throws Exception {
        Error actual = new Error("test","test","test","test");
        Error other = new Error("tset","tset","tset","tset");
        // @formatter:off
        assertTrue(new Error().equals(new Error()));
        assertTrue(actual.equals(actual));
        assertFalse(actual.equals(null));
        assertFalse(new Error().equals(null));
        assertFalse(new Error().equals(new Object()));
        assertFalse(actual.equals(new Object()));
        assertFalse(actual.equals(other));
        // @formatter:on
    }

    @Test
    @DisplayName("test ssn equals")
    public void whenCreateSsn_ThenEqualsOk() throws Exception {
        SocialSecurityNumber actual = new SocialSecurityNumber("111-11-1111");
        SocialSecurityNumber other = new SocialSecurityNumber("222-22-2222");
        // @formatter:off
        assertTrue(actual.equals(actual));
        assertFalse(actual.equals(null));
        assertFalse(actual.equals(other));
        assertFalse(new SocialSecurityNumber().equals(null));
        assertFalse(new SocialSecurityNumber().equals(new Object()));
        // @formatter:on
    }

    @Test
    @DisplayName("test ssn as string")
    public void whenGetSsnAsString_ThenExpected() throws Exception {
        // @formatter:off
        SocialSecurityNumber actual = new SocialSecurityNumber("111-11-1111");
        String expected = "111-11-1111";
        assertEquals(expected, actual.getAsString());
        // @formatter:on
    }

    @Test
    @DisplayName("test numbered ssn as string")
    public void whenCreateSsnAsNumbers_ThenGetAsStringExpected() throws Exception {
        // @formatter:off
        SocialSecurityNumber actual = new SocialSecurityNumber(111,11,1111);
        String expected = "111-11-1111";
        assertEquals(expected, actual.getAsString());
        // @formatter:on
    }

    @Test
    @DisplayName("test null ssn as empty string")
    public void whenGetNullSsnAsString_ThenExpected() throws Exception {
        // @formatter:off
        SocialSecurityNumber actual = new SocialSecurityNumber();
        String expected = "--";
        assertEquals(expected, actual.getAsString());
        // @formatter:on
    }

    @ParameterizedTest(name = ("social security number test"))
    @DisplayName("test valid SSN creation")
    @ValueSource(strings = { "123-45-6789", "100-10-1000" })
    public void whenCreateValidSsn_ThenIsValid(String ssn) throws Exception {
        SocialSecurityNumber actual = new SocialSecurityNumber(ssn);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
            () -> assertNotNull(actual.getSsn()),
            () -> assertNotNull(actual.getAsString()),
            () -> assertTrue(actual.isValid()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test SSN hashcode")
    @ValueSource(strings = { "123-45-6789", "100-10-1000" })
    public void whenCreateSsn_ThenHashCodeAndToStringAreOk(String ssn) throws Exception {
        SocialSecurityNumber actual = new SocialSecurityNumber(ssn);
        SocialSecurityNumber expected = new SocialSecurityNumber(ssn);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
                () -> assertNotNull(expected),
                () -> assertEquals(expected, actual),
                () -> assertNotNull(expected.hashCode()),
                () -> assertEquals(expected.toString(), actual.toString()),
                () -> assertEquals(expected.hashCode(), actual.hashCode()),
                () -> assertFalse(expected.equals(null)));
       // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test invalid ssn")
    @ValueSource(strings = { "123-", "001-10-1000" })
    public void whenCreateInvalidSsn_ThenIsNotValid(String ssn) throws Exception {
        SocialSecurityNumber actual = new SocialSecurityNumber(ssn);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
            () -> assertFalse(actual.isValid()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("invalid social security number setter test")
    @ValueSource(strings = { "123-", "000-10-1000", "", "1" })
    public void whenCreateNoargsConstructorSsnAndUseSetter_ThenIsNotValid(String ssn) throws Exception {
        SocialSecurityNumber actual = new SocialSecurityNumber();
        actual.setSsn(ssn);
        // @formatter:off
        assertAll(
            () -> assertNotNull(actual),
            () -> assertFalse(actual.isValid()));
        // @formatter:on
    }

    @Test
    @DisplayName("test empty ssn hashcode")
    public void whenCreateDefaultSsn_ThenHashCodeFunctionIsStillOk() throws Exception {
        // @formatter:off
        assertAll(() -> assertEquals(new SocialSecurityNumber(), new SocialSecurityNumber()),
                  () -> assertEquals(new SocialSecurityNumber().hashCode(), new SocialSecurityNumber().hashCode()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test audit getters")
    @CsvSource("2,1,com.tnsilver.Blah,test,Blah,tester,1980-08-31 09:31.59")
    public void whenCreateAudit_ThenGettersGetExpectedValues(Long id, Long entityId, String entityType, String actionType,
        String entity, String createdBy, String timeStamp) throws Exception {
        LocalDateTime createdOn = LocalDateTime.parse(timeStamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm.ss"));
        assertNotNull(createdOn);
        Audit actual = new Audit(entityId, entityType, actionType, entity);
        actual.setId(id);
        actual.setCreatedOn(createdOn);
        actual.setCreatedBy(createdBy);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
            () -> assertEquals(id, actual.getId()),
            () -> assertEquals(entityId, actual.getEntityId()),
            () -> assertEquals(entityType, actual.getEntityType()),
            () -> assertEquals(actionType, actual.getActionType()),
            () -> assertEquals(entity, actual.getEntity()),
            () -> assertEquals(createdOn, actual.getCreatedOn()),
            () -> assertEquals(createdBy, actual.getCreatedBy()));
       // @formatter:on

    }

    @ParameterizedTest
    @DisplayName("test audit hashcode")
    @CsvSource("2,1,com.tnsilver.Blah,test,Blah,tester")
    public void whenCreateAudit_ThenHashcodeWorksOk(Long id, Long entityId, String entityType, String actionType, String entity,
        String inceptor) throws Exception {
        Audit actual = new Audit(entityId, entityType, actionType, entity);
        Audit expected = new Audit(entityId, entityType, actionType, entity);
        expected.setCreatedBy(actual.getCreatedBy());
        expected.setCreatedOn(actual.getCreatedOn());
        // @formatter:off
        assertAll(() ->  assertNotNull(actual),
            () -> assertNotNull(expected),
            () -> assertEquals(expected.toString(), actual.toString()),
            () -> assertEquals(expected.hashCode(), actual.hashCode()));
        // @formatter:on
    }

    @Test
    @DisplayName("test create noargs constructor audit")
    public void whenCreateNoArgsConstructorAudit_ThenOk() throws Exception {
        // @formatter:off
        assertAll(() -> assertEquals(new Audit(), new Audit()),
            () -> assertEquals(new Audit().hashCode(), new Audit().hashCode()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test error entity getters")
    @CsvSource("com.tnsilver.Blah,test,blah,bad value blah")
    public void whenCreateError_ThenGettersGetExpectedValues(String entity, String property, String invalidValue, String message)
        throws Exception {
        Error actual = new Error(entity, property, invalidValue, message);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
            () -> assertEquals(entity, actual.getEntity()),
            () -> assertEquals(property, actual.getProperty()),
            () -> assertEquals(invalidValue, actual.getInvalidValue()),
            () -> assertEquals(message, actual.getMessage()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test error entity hashcode")
    @CsvSource("com.tnsilver.Blah,test,blah,bad value blah")
    public void whenCreateError_ThenHashcodeWorksOk(String entity, String property, String invalidValue, String message)
        throws Exception {
        Error actual = new Error(entity, property, invalidValue, message);
        Error expected = new Error(entity, property, invalidValue, message);
        // @formatter:off
        assertAll(() -> assertNotNull(actual),
                () -> assertNotNull(expected),
                () -> assertEquals(expected, actual),
                () -> assertNotNull(expected.hashCode()),
                () -> assertEquals(expected.toString(), actual.toString()),
                () -> assertEquals(expected.hashCode(), actual.hashCode()),
                () -> assertFalse(expected.equals(null)));
        // @formatter:on
    }

    @Test
    @DisplayName("test noargs constructor error hashcode")
    public void whenCreateNoArgsError_ThenHashcodeStillWorksOk() throws Exception {
        // @formatter:off
        assertAll(() -> assertEquals(new Error(), new Error()),
            () -> assertEquals(new Error().hashCode(), new Error().hashCode()));
        // @formatter:on
    }



}
