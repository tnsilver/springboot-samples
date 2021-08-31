/*
 * File: ContactModelTest.java
 * Creation Date: Jul 9, 2021
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
package com.tnsilver.contacts.hateoas;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.tnsilver.contacts.base.BaseRedisTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.SocialSecurityNumber;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContactModelTest extends BaseRedisTest {

    @Autowired
    private ContactModelAssembler assembler;
    // @formatter:off
    private Contact test1 = new Contact(101L, new SocialSecurityNumber("555-55-5555"), "test1", "tester1", LocalDate.now(), false, 0);
    private Contact test2 = new Contact(202L, new SocialSecurityNumber("666-66-6666"), "test2", "tester2", LocalDate.now(), true, 3);
    // @formatter:on

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseRedisTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @Test
    @DisplayName("test different model hashcodes")
    void whenTwoDifferentModels_ThenTwoDifferentHashCodes() {
        ContactModel model1 = assembler.toModel(test1);
        ContactModel model2 = assembler.toModel(test2);
        assertNotEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    @DisplayName("test same model hashcode")
    void whenSameModel_ThenSameHashCodes() {
        ContactModel model1 = assembler.toModel(test1);
        ContactModel model2 = assembler.toModel(test1);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    @DisplayName("test different models not equal")
    void whenTwoDifferentModels_ThenNotEquals() {
        ContactModel model1 = assembler.toModel(test1);
        ContactModel model2 = assembler.toModel(test2);
        assertFalse(model1.equals(model2));
    }

    @Test
    @DisplayName("test same models equal")
    void whenSameModels_ThenEquals() {
        ContactModel model1 = assembler.toModel(test1);
        ContactModel model2 = assembler.toModel(test1);
        assertTrue(model1.equals(model2));
    }

    @Test
    @DisplayName("test model not equal null")
    void whenModelComparedToNull_ThenNotEquals() {
        ContactModel model1 = assembler.toModel(test1);
        assertFalse(model1.equals(null));
    }

    @Test
    @DisplayName("test model to string")
    void whenCallToString_ThenAllFieldsIncluded() {
        ContactModel model1 = assembler.toModel(test1);
        String birthDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // @formatter:off
        assertAll(() -> assertThat(model1.toString(), containsString("ssn=555-55-5555")),
                  () -> assertThat(model1.toString(), containsString("firstName=test1")),
                  () -> assertThat(model1.toString(), containsString("lastName=tester1")),
                  () -> assertThat(model1.toString(), containsString("birthDate=" + birthDate)),
                  () -> assertThat(model1.toString(), containsString("married=false")),
                  () -> assertThat(model1.toString(), containsString("children=0")));
        // @formatter:on
    }

    @Test
    @DisplayName("test collection model")
    void whenAssemblingCollectionModel_ThenIterable() {
        ContactModel model1 = assembler.toModel(test1);
        ContactModel model2 = assembler.toModel(test2);
        List<Contact> contacts = List.of(test1, test2);
        Iterable<ContactModel> actual = assembler.toCollectionModel(contacts);
        assertThat(actual, containsInAnyOrder(model1, model2));
    }
}
