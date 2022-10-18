/*
 * File: SocialSecurityNumberConverterTest.scala
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
package com.tnsilver.contacts.converter

import com.tnsilver.contacts.base.BaseJpaTest
import com.tnsilver.contacts.model.SocialSecurityNumber
import org.junit.jupiter.api.Assertions.{assertEquals, assertTrue}
import org.junit.jupiter.api.{DisplayName, Test}
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * The test SocialSecurityNumberConverterTest tests the application converter {@link SocialSecurityNumberConverter}
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest class SocialSecurityNumberConverterTest extends BaseJpaTest {

  @Autowired private val ssnConverter: SocialSecurityNumberConverter = null

  @ParameterizedTest
  @DisplayName("test good SSN conversion")
  @CsvSource(Array("123-45-6789", "100-10-1000"))
  @throws[Exception]
  def whenCreateStringBasedSsn_ThenConverterConvertsTheSame(ssn: String): Unit = {
    val actual: SocialSecurityNumber = ssnConverter.convert(ssn)
    assertEquals(new SocialSecurityNumber(ssn), actual)
    assertTrue(actual.isValid)
  }

  @ParameterizedTest
  @DisplayName("test malformed SSN conversion")
  @CsvSource(Array("123-", "-10-1000"))
  @throws[Exception]
  def whenCreateBadStringSsn_ThenConverterConverts(ssn: String): Unit = {
    assertEquals(new SocialSecurityNumber(ssn), ssnConverter.convert(ssn))
  }

  @ParameterizedTest
  @DisplayName("test numbered SSN conversion")
  @CsvSource(Array("123,12,1234,123-12-1234", "123,45,6789,123-45-6789"))
  @throws[Exception]
  def whenCreateBadNumberedSsn_ThenConverterConverts(area: Integer, group: Integer, serial: Integer, expected: String): Unit = {
    val ssn = String.format("%s-%s-%s", area, group, serial)
    assertEquals(new SocialSecurityNumber(area, group, serial), ssnConverter.convert(ssn))
  }

  @Test
  @DisplayName("test null SSN conversion")
  @throws[Exception]
  def whenCreateNullSsn_ThenConverterConverts(): Unit = {
    assertEquals(new SocialSecurityNumber, ssnConverter.convert(null))
  }
}
