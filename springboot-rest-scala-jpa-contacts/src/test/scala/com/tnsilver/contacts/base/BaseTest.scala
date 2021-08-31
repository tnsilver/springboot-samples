package com.tnsilver.contacts.base

import org.junit.jupiter.api.{BeforeAll, BeforeEach, TestInfo}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

import java.time.ZoneId
import java.util.TimeZone
import java.util.stream.{Collectors, Stream}
import java.util.stream.Stream

/*
 * File: BaseTest.scala
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
class BaseTest {

  protected val NUM_OF_CONTACTS : Integer = 34
  protected val DATABASE : String = "contacts"

  protected var logger: Logger = _

  @Autowired  var env : Environment = _

  @BeforeAll
  @throws[Exception]
  def beforeAll(): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Z")))
  }

  @BeforeEach
  @throws[Exception]
  def beforeEach(info: TestInfo): Unit = {
    logger = LoggerFactory.getLogger(getClass)
    var profile = env.getActiveProfiles.toList.mkString(",")
    logger.debug("\n\nPROFILE: '{}', ENTERING '{}':\n", profile, info.getDisplayName)
  }
}
