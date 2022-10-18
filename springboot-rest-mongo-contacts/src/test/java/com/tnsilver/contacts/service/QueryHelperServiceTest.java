/*
 * File: QueryHelperServiceTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tnsilver.contacts.base.BaseMongoTest;

@SpringBootTest
public class QueryHelperServiceTest extends BaseMongoTest {

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @Autowired
    QueryHelperService service = new QueryHelperService();
    private static final SimpleDateFormat dateTimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm.ss");

    /*@ParameterizedTest
    @DisplayName("test localDateTime to start-of-day")
    // @formatter:off
    @CsvSource({ "1978-06-20T05:05:43.999,1978-06-20 00:00.00",
                 "1969-02-06T01:22:11.234,1969-02-06 00:00.00",
                 "0001-01-01T00:00:00.000,0001-01-01 00:00.00" })
    // @formatter:om
    public void whenGivenDateTime_ThenReturnStartOfDayDate(LocalDateTime localDateTime, String expected) {
        Date date = service.atStartOfDay(localDateTime);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }*/

    @ParameterizedTest
    @DisplayName("test LocalDate to start-of-day")
    // @formatter:off
    @CsvSource({ "1978-06-20,1978-06-20 00:00.00",
                 "1969-02-06,1969-02-06 00:00.00",
                 "0001-01-01,0001-01-01 00:00.00" })
    // @formatter:on
    public void whenGivenDate_ThenReturnStartOfDayDate(LocalDate localDate, String expected) {
        Date date = service.atStartOfDay(localDate);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }

    /*@ParameterizedTest
    @DisplayName("test localDateTime to end-of-day")
    // @formatter:off
    @CsvSource({ "1978-06-20T05:05:43.999,1978-06-20 23:59.59",
                 "1969-02-06T01:22:11.234,1969-02-06 23:59.59",
                 "0001-01-01T00:00:00.000,0001-01-01 23:59.59" })
    // @formatter:on
    public void whenGivenDateTime_ThenReturnEndOfDayDate(LocalDateTime localDateTime, String expected) {
        Date date = service.atEndOfDay(localDateTime);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }*/

    @ParameterizedTest
    @DisplayName("test LocalDate to end-of-day")
    // @formatter:off
    @CsvSource({ "1978-06-20,1978-06-20 23:59.59",
                 "1969-02-06,1969-02-06 23:59.59",
                 "0001-01-01,0001-01-01 23:59.59" })
    // @formatter:on
    public void whenGivenDate_ThenReturnEndOfDayDate(LocalDate localDate, String expected) {
        Date date = service.atEndOfDay(localDate);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }

    /*@ParameterizedTest
    @DisplayName("test LocalDateTime to from-date")
    // @formatter:off
    @CsvSource({ "1978-06-20T05:05:43.999,1978-06-20 00:00.00",
                 "1969-02-06T01:22:11.234,1969-02-06 00:00.00",
                 "0001-01-01T00:00:00.000,0001-01-01 00:00.00" })
    // @formatter:on
    public void whenGivenDateTime_ThenReturnTruncatedFromDate(LocalDateTime localDateTime, String expected) {
        Date date = service.fromDate(localDateTime);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }*/

    @ParameterizedTest
    @DisplayName("test LocalDate to from-date")
    // @formatter:off
    @CsvSource({ "1978-06-20,1978-06-20 00:00.00",
                 "1969-02-06,1969-02-06 00:00.00",
                 "0001-01-01,0001-01-01 00:00.00" })
    // @formatter:on
    public void whenGivenDate_ThenReturnTruncatedFromDate(LocalDate localDate, String expected) {
        Date date = service.fromDate(localDate);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }

    /*@ParameterizedTest
    @DisplayName("test LocalDateTime to to-date")
    // @formatter:off
    @CsvSource({ "1978-06-20T05:05:43.999,1978-06-20 23:59.59",
                 "1969-02-06T01:22:11.234,1969-02-06 23:59.59",
                 "0001-01-01T00:00:00.000,0001-01-01 23:59.59" })
    // @formatter:on
    public void whenGivenDateTime_ThenReturnTruncatedToDate(LocalDateTime localDateTime, String expected) {
        Date date = service.toDate(localDateTime);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }*/

    @ParameterizedTest
    @DisplayName("test LocalDate to to-date")
    // @formatter:off
    @CsvSource({ "1978-06-20,1978-06-20 23:59.59",
                 "1969-02-06,1969-02-06 23:59.59",
                 "0001-01-01,0001-01-01 23:59.59" })
    // @formatter:on
    public void whenGivenDate_ThenReturnTruncatedToDate(LocalDate localDate, String expected) {
        Date date = service.toDate(localDate);
        String actual = dateTimeformat.format(date);
        assertEquals(expected, actual);
    }

    /*@Test
    @DisplayName("test LocalDateTime to from-date with null")
    public void whenGivenNullDateTimeToFromDate_ThenReturnEarliestBCDate() {
        LocalDateTime dateTime = null;
        Date date = service.fromDate(dateTime);
        String actual = dateTimeformat.format(date);
        assertEquals("0001-01-01 00:00.00", actual);
    }*/

    @Test
    @DisplayName("test LocalDate to from-date with null")
    public void whenGivenNullDateToFromDate_ThenReturnEarliestBCDate() {
        LocalDate date = null;
        Date fromDate = service.fromDate(date);
        String actual = dateTimeformat.format(fromDate);
        assertEquals("0001-01-01 00:00.00", actual);
    }

    /*@Test
    @DisplayName("test LocalDateTime to to-date with null")
    public void whenGivenNullDateTimeToDate_ThenReturnNextMillenium() {
        LocalDateTime dateTime = null;
        Date date = service.toDate(dateTime);
        String actual = dateTimeformat.format(date);
        assertEquals("3000-01-01 00:00.00", actual);
    }*/

    @Test
    @DisplayName("test LocalDate to to-date with null")
    public void whenGivenNullDateToDate_ThenReturnNextMillenium() {
        LocalDate dateTime = null;
        Date toDate = service.toDate(dateTime);
        String actual = dateTimeformat.format(toDate);
        assertEquals("3000-01-01 00:00.00", actual);
    }

    @ParameterizedTest
    @DisplayName("test regex")
    // @formatter:off
    @CsvSource({ "Silverman,^Silverman.*",
                 "Silve,^Silve.*",
                 "'',^.*",
                 "&,^.*"})
    // @formatter:on
    public void whenGivenString_ThenReturnExpectedRegex(String value, String expected) {
        String actual = service.regex(value.isBlank() ? null : value.equals("&") ? "" : value);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test booleans")
    // @formatter:off
    @CsvSource(value={ "true@[true]",
                       "false@[false]",
                       "''@[true, false]" }, delimiter = '@')
    // @formatter:on
    public void whenGivenBoolean_ThenReturnExpectedCollection(String value, String expected) {
        Boolean bool = value.isBlank() ? null : Boolean.valueOf(value);
        Collection<Boolean> actual = service.booleans(bool);
        assertEquals(expected, List.copyOf(actual).toString());
    }

    @ParameterizedTest
    @DisplayName("test ints")
    // @formatter:off
    @CsvSource({ "1,2,1",
                 "1,-2,1",
                 "'',2,2" })
    // @formatter:on
    public void whenGivenInt_ThenReturnExpectedRange(String value, Integer boundary, Integer expected) {
        Integer num = value.isBlank() ? null : Integer.parseInt(value);
        Integer actual = service.ints(num,boundary);
        assertEquals(expected, actual);
    }
}
