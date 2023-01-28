/*
 * File: RepositoryPopulatorTest.java
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
package com.tnsilver.contacts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tnsilver.contacts.base.BaseJpaTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.repository.ContactRepository;

@SpringBootTest
class RepositoryPopulatorTest extends BaseJpaTest {

    @Autowired private ContactRepository contactRepository;

    @BeforeAll
    public static void beforeAll(TestInfo info) throws Exception {
        BaseJpaTest.beforeAll();
    }

    @BeforeEach
    public void init(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @Test
    @DisplayName("test populator is working")
    public void whenContextLoadsAndCollectionEmpty_ThenPopulateCollection() {
        List<Contact> contacts = contactRepository.findAll();
        assertEquals(NUM_OF_CONTACTS, contacts.size());
    }

}
