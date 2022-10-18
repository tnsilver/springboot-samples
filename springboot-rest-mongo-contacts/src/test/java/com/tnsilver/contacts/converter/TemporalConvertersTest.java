/*
 * File: TemporalConvertersTest.java
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
package com.tnsilver.contacts.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.tnsilver.contacts.base.BaseMongoTest;

/**
 * The test TemporalConvertersTest tests the application temporal converters {@link LocalDateTimeConverter} and
 * {@link LocalDateConverter}
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
// @Transactional
public class TemporalConvertersTest extends BaseMongoTest {

    @Resource
    private Environment env;
    @Resource
    private LocalDateConverter localDateConverter;
    @Resource
    private LocalDateTimeConverter localDateTimeConverter;

    @ParameterizedTest
    @DisplayName("test convert dates with patterns")
    // @formatter:off
    @CsvSource({ "1981-08-31,yyyy-MM-dd",
                 "1981-31-08,yyyy-dd-MM",
                 "31-08-1980,dd-MM-yyyy",
                 "08-31-1980,MM-dd-yyyy",
                 "08/31/1980,MM/dd/yyyy",
                 "31/08/1980,dd/MM/yyyy" })
    // @formatter:on
    public void whenGivenDateStringAndPattern_ThenConverterConvertsLikeLocalDate(String date, String pattern) throws Exception {
        assertEquals(LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)), localDateConverter.convert(date));
    }

    @Test
    @DisplayName("test convert bad date")
    public void whenGivenBadPatternDateString_ThenException() throws Exception {
        assertThrows(DateTimeException.class, () -> localDateConverter.convert("1981,08,31"));
    }

    @ParameterizedTest
    @DisplayName("test convert datetimes with patterns")
    // @formatter:off
    @CsvSource({ "1981-08-31 09:31.59,yyyy-MM-dd HH:mm.ss",
                 "1981-31-08 09:31.59,yyyy-dd-MM HH:mm.ss",
                 "31-08-1980 09:31.59,dd-MM-yyyy HH:mm.ss",
                 "08-31-1980 09:31.59,MM-dd-yyyy HH:mm.ss",
                 "08/31/1980 09:31.59,MM/dd/yyyy HH:mm.ss" })
    // @formatter:on
    public void whenGivenDatetimeStringAndPattern_ThenConverterConvertsLikeLocalDateTime(String date, String pattern)
        throws Exception {
        assertEquals(LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern)), localDateTimeConverter.convert(date));
    }

    @Test
    @DisplayName("test convert bad datetime")
    public void whenGivenBadPatternDatetimeString_ThenConverterReturnNull() throws Exception {
        assertThrows(DateTimeException.class, () -> localDateTimeConverter.convert("1981,08,31 09:31.59"));
    }
}
