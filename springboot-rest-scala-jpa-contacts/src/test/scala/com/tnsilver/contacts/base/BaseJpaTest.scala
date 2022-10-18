package com.tnsilver.contacts.base

import org.junit.jupiter.api.TestInstance.Lifecycle

import java.util
import java.util.stream.Collectors
import org.junit.jupiter.api.{BeforeAll, BeforeEach, TestInfo, TestInstance}
import org.springframework.core.env.AbstractEnvironment
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

/*
 * File: BaseJpaTest.scala
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
@ActiveProfiles(Array("test"))
@TestInstance(Lifecycle.PER_CLASS)
@Transactional abstract class BaseJpaTest extends BaseTest {

  @BeforeAll
  @throws[Exception]
  override def beforeAll(): Unit = {
    super.beforeAll()
    val profiles = classOf[BaseJpaTest].getDeclaredAnnotation(classOf[ActiveProfiles]).value
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, util.Arrays.stream(profiles).collect(Collectors.joining(",")))
  }

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
  }
}
