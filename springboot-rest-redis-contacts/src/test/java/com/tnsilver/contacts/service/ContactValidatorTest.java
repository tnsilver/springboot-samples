/*
 * File: ContactValidatorTest.java
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;

import com.tnsilver.contacts.base.BaseRedisTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.SocialSecurityNumber;
import com.tnsilver.contacts.validator.ContactValidator;

/**
 * The test ContactValidatorTest tests the {@link ContactValidator} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
public class ContactValidatorTest extends BaseRedisTest {

    private static final Logger logger = LoggerFactory.getLogger(ContactValidatorTest.class);
    @Resource
    private ContactValidator contactValidator;

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @ParameterizedTest
    @DisplayName("test validator with valid contact")
    @CsvSource("Test,Contact,123-45-6789,1980-08-31,true,2")
    public void whenCreatedValidContact_ThenValid(String firstName, String lastName, SocialSecurityNumber ssn, LocalDate birthDate,
        Boolean married, Integer children) throws Exception {
        Contact contact = new Contact(ssn, firstName, lastName, birthDate, married, children);
        BindException errors = new BindException(contact, "contact");
        contactValidator.validate(contact, errors);
        assertTrue(errors.getAllErrors().isEmpty());
    }

    @ParameterizedTest
    @DisplayName("test validator with invalid ssn")
    @CsvSource("Test,Contact,123-45-6,1980-08-31,true,2")
    public void whenCreatedContactWithInvalidSsn_ThenInvalid(String firstName, String lastName, SocialSecurityNumber ssn,
        LocalDate birthDate, Boolean married, Integer children) throws Exception {
        Contact contact = new Contact(ssn, firstName, lastName, birthDate, married, children);
        BindException errors = new BindException(contact, "contact");
        contactValidator.validate(contact, errors);
        // @formatter:off
        assertAll(() -> assertTrue(contactValidator.supports(Contact.class)),
                () -> assertFalse(errors.getAllErrors().isEmpty()),
                () -> assertFalse(errors.getFieldErrors().isEmpty()),
                () -> assertEquals("invalid ssn", errors.getFieldError("ssn").getDefaultMessage()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test validator with invalid names")
    @CsvSource("T,C,123-45-6789,1980-08-31,true,2")
    public void whenCreatedContactWithInvalidNames_ThenInvalid(String firstName, String lastName, SocialSecurityNumber ssn,
        LocalDate birthDate, Boolean married, Integer children) throws Exception {
        Contact contact = new Contact(ssn, firstName, lastName, birthDate, married, children);
        BindException errors = new BindException(contact, "contact");
        contactValidator.validate(contact, errors);
        // @formatter:off
        assertAll(() -> assertTrue(contactValidator.supports(Contact.class)),
                () -> assertFalse(errors.getAllErrors().isEmpty()),
                () -> assertFalse(errors.getFieldErrors().isEmpty()),
                () -> assertEquals("invalid name", errors.getFieldError("firstName").getDefaultMessage()),
                () -> assertEquals("invalid name", errors.getFieldError("lastName").getDefaultMessage()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test validator with null birth date")
    @CsvSource("Test,Contact,123-45-6789,true,2")
    public void whenCreatedContactWithNullBirthdate_ThenInvalid(String firstName, String lastName, SocialSecurityNumber ssn,
        Boolean married, Integer children) throws Exception {
        Contact contact = new Contact(ssn, firstName, lastName, null, married, children);
        BindException errors = new BindException(contact, "contact");
        contactValidator.validate(contact, errors);
        errors.getFieldErrors().forEach(error -> logger.debug("Error for field {} -> {}", error.getField(), error.getDefaultMessage()));
        // @formatter:off
        assertAll(() -> assertTrue(contactValidator.supports(Contact.class)),
                () -> assertFalse(errors.getAllErrors().isEmpty()),
                () -> assertFalse(errors.getFieldErrors().isEmpty()),
                () -> assertEquals("invalid birth date", errors.getFieldError("birthDate").getDefaultMessage()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test validator with invalid children")
    @CsvSource("T,C,123-45-6789,1980-08-31,true,-1")
    public void whenCreatedContactWithNegativeChildren_ThenInvalid(String firstName, String lastName, SocialSecurityNumber ssn,
        LocalDate birthDate, Boolean married, Integer children) throws Exception {
        Contact contact = new Contact(ssn, firstName, lastName, birthDate, married, children);
        BindException errors = new BindException(contact, "contact");
        contactValidator.validate(contact, errors);
        // @formatter:off
        assertAll(() -> assertTrue(contactValidator.supports(Contact.class)),
                () -> assertFalse(errors.getAllErrors().isEmpty()),
                () -> assertFalse(errors.getFieldErrors().isEmpty()),
                () -> assertEquals("invalid number of children", errors.getFieldError("children").getDefaultMessage()));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test validator with null married")
    @CsvSource("T,C,123-45-6789,1980-08-31,1")
    public void whenCreatedContactWithNullMarried_ThenInvalid(String firstName, String lastName, SocialSecurityNumber ssn,
        LocalDate birthDate, Integer children) throws Exception {
        Contact contact = new Contact(ssn, firstName, lastName, birthDate, null, children);
        BindException errors = new BindException(contact, "contact");
        contactValidator.validate(contact, errors);
        // @formatter:off
        assertAll(() -> assertTrue(contactValidator.supports(Contact.class)),
                () -> assertFalse(errors.getAllErrors().isEmpty()),
                () -> assertFalse(errors.getFieldErrors().isEmpty()),
                () -> assertEquals("invalid maritial status", errors.getFieldError("married").getDefaultMessage()));
       // @formatter:on
    }

    @Test
    @DisplayName("test validator with null contact")
    public void whenNullContact_ThenInvalid() throws Exception {
        Contact contact = null;
        BindException errors = new BindException(new Contact(), "contact");
        contactValidator.validate(contact, errors);
        // @formatter:off
        assertTrue(contactValidator.supports(Contact.class));
        assertFalse(errors.getAllErrors().isEmpty());
        assertEquals(errors.getGlobalError().getDefaultMessage(), "contact is null");
       // @formatter:on
    }
}
