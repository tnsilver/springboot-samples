/*
 * File: QueryHelperService.java
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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

@Service("qHelp")
public class QueryHelperService {

    /**
     * formats a mongodb regular expression out of a possibly null or empty string value
     * @param value the possibly null or empty string value
     * @return mongodb regular expression to find the value in the beginning of the string
     */
    public String regex(String value) {
        return String.format("^%s.*", (null == value || value.isBlank()) ? "" : value);
    }

    /**
     * Creates a list of boolean values out of a possibly null Boolean value.
     * Creates a list of [true,false] if the value is null, other wise either [true] or [false], depending on the value.
     * @param value a possibly null Boolean value
     * @return a list of boolean values (e.g [true,false])
     */
    public Collection<Boolean> booleans(Boolean value) {
        return null == value ? List.of(true, false) : List.of(value);
    }

    /**
     * returns an int value or {@code boundary} if {@code value} is null
     * @param value an int value, possibly null
     * @param boundary the boundary to return if {@code value} is null
     * @return an int value or {@code boundary} if {@code value} is null
     */
    public Integer ints(Integer value, int boundary) {
        return null == value ? boundary : value;
    }

    /**
     * returns a Date suitable for criteria {@code gt} or {@code gte} operators.
     * if {@code value} is null, this method returns the start of day of 0000-01-01 (year '0').
     * If {@code value} is not null, this method truncates the {@code value} at zone UTC to the start of the day.
     * @param value the local date value, possibly null
     * @return if value is not null returns a Date representing the value's start of day in UTC zone,
     * otherwise returns a Date representing the start of day of year '0'
     */
    /*public Date fromDate(LocalDateTime value) {
        if (null != value)
            return atStartOfDay(value);
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 1, 0, 0, 0);
        return cal.getTime();
    }*/

    public Date fromDate(LocalDate value) {
        if (null != value)
            return atStartOfDay(value);
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * returns a Date suitable for criteria {@code lt} or {@code lte} operators.
     * if {@code value} is null, this method returns the start of day of 3000-01-01 (year '3000').
     * If {@code value} is not null, this method truncates the {@code value} at zone UTC to the end of the day.
     * @param value the local date value, possibly null
     * @return if value is not null returns a Date representing the value's end of day in UTC zone,
     * otherwise returns a Date representing the start of day of year '3000'
     */
    /*public Date toDate(LocalDateTime value) {
        if (null != value)
            return atEndOfDay(value);
        LocalDateTime late = LocalDateTime.parse("3000-01-01T00:00:00");
        return atStartOfDay(late);
    }*/

    public Date toDate(LocalDate value) {
        if (null != value)
            return atEndOfDay(value);
        LocalDate late = LocalDate.parse("3000-01-01");
        return atStartOfDay(late);
    }

    /*public Date atStartOfDay(LocalDateTime value) {
        Calendar cal = Calendar.getInstance();
        cal.set(value.getYear(), value.getMonth().getValue()-1,value.getDayOfMonth(), 0, 0, 0);
        return cal.getTime();
    }*/

    public Date atStartOfDay(LocalDate value) {
        Calendar cal = Calendar.getInstance();
        cal.set(value.getYear(), value.getMonth().getValue()-1,value.getDayOfMonth(), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /*public Date atEndOfDay(LocalDateTime value) {
        Calendar cal = Calendar.getInstance();
        cal.set(value.getYear(), value.getMonth().getValue()-1,value.getDayOfMonth(), 23, 59, 59);
        return cal.getTime();
    }*/

    public Date atEndOfDay(LocalDate value) {
        Calendar cal = Calendar.getInstance();
        cal.set(value.getYear(), value.getMonth().getValue()-1,value.getDayOfMonth(), 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /*public static void main(String[] args) {
        QueryHelperService service = new QueryHelperService();
        LocalDate dateTime = LocalDate.parse("1978-06-20");
        System.out.printf("dateTime: %s%n",dateTime);
        System.out.printf("startOfDay: %s%n",service.atStartOfDay(dateTime));
        System.out.printf("endOfDay: %s%n",service.atEndOfDay(dateTime));
        System.out.printf("from: %s%n",service.fromDate(dateTime));
        System.out.printf("to: %s%n",service.toDate(dateTime));
        dateTime = null;
        System.out.printf("from null: %s%n",service.fromDate(dateTime));
        System.out.printf("to null: %s%n",service.toDate(dateTime));
    }*/
}
