/*
 * File: RepositoryPopulatorTest.scala
 * Creation Date: Jun 25, 2021
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
package com.tnsilver.contacts.service

import org.junit.jupiter.api.Assertions.assertEquals
import java.util
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import com.tnsilver.contacts.base.BaseJpaTest
import com.tnsilver.contacts.model.Contact
import com.tnsilver.contacts.repository.ContactRepository




@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest class RepositoryPopulatorTest extends BaseJpaTest {

  @Autowired private val contactRepository : ContactRepository = null

  @BeforeAll
  @throws[Exception]
  def beforeAll(info: TestInfo): Unit = {
    super.beforeAll()
  }

  @BeforeEach
  @throws[Exception]
  def init(info: TestInfo): Unit = {
    super.beforeEach(info)
  }

  @Test
  @DisplayName("test populator is working") def whenContextLoadsAndCollectionEmpty_ThenPopulateCollection(): Unit = {
    val contacts = contactRepository.findAll
    assertEquals(NUM_OF_CONTACTS, contacts.size)
  }
}
