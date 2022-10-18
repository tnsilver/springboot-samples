/*
 * File: TemporalConvertersTest.scala
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
import org.junit.jupiter.api.Assertions.{assertEquals, assertThrows, assertNull}
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.{Assertions, DisplayName, Test}
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

import java.time.format.DateTimeFormatter
import java.time.{DateTimeException, LocalDate, LocalDateTime}
import javax.annotation.Resource

/**
 * The test TemporalConvertersTest tests the application temporal converters {@link LocalDateTimeConverter} and
 * {@link LocalDateConverter}
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest class TemporalConvertersTest extends BaseJpaTest {

  @Resource private val localDateConverter: LocalDateConverter = null
  @Resource private val localDateTimeConverter: LocalDateTimeConverter = null

  @ParameterizedTest
  @DisplayName("test convert dates with patterns") // @formatter:off
  @CsvSource(Array("1981-08-31,yyyy-MM-dd", "1981-31-08,yyyy-dd-MM", "31-08-1980,dd-MM-yyyy", "08-31-1980,MM-dd-yyyy", "08/31/1980,MM/dd/yyyy", "31/08/1980,dd/MM/yyyy"))
  @throws[Exception]
  // @formatter:on
  def whenGivenDateStringAndPattern_ThenConverterConvertsLikeLocalDate(date: String, pattern: String): Unit = {
    assertEquals(LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)), localDateConverter.convert(date))
  }

  @Test
  @DisplayName("test convert bad date")
  @throws[Exception]
  def whenGivenBadPatternDateString_ThenException(): Unit = {
    assertNull(localDateConverter.convert("1981,08,31"))
  }

  @ParameterizedTest
  @DisplayName("test convert datetimes with patterns")
  @CsvSource(Array("1981-08-31 09:31.59,yyyy-MM-dd HH:mm.ss", "1981-31-08 09:31.59,yyyy-dd-MM HH:mm.ss", "31-08-1980 09:31.59,dd-MM-yyyy HH:mm.ss", "08-31-1980 09:31.59,MM-dd-yyyy HH:mm.ss", "08/31/1980 09:31.59,MM/dd/yyyy HH:mm.ss"))
  @throws[Exception]
  def whenGivenDatetimeStringAndPattern_ThenConverterConvertsLikeLocalDateTime(date: String, pattern: String): Unit = {
    assertEquals(LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern)), localDateTimeConverter.convert(date))
  }

  @Test
  @DisplayName("test convert bad datetime")
  @throws[Exception]
  def whenGivenBadPatternDatetimeString_ThenConverterReturnNull(): Unit = {
    assertNull(localDateTimeConverter.convert("1981,08,31 09:31.59"))
  }
}
