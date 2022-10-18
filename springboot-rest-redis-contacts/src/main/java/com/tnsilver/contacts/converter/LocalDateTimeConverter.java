/*
 * File: LocalDateTimeConverter.java
 * Creation Date: Jul 8, 2021
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Value("#{'${datetime.patterns.csv}'.split(',')}")
    private List<String> patterns;
    private List<DateTimeFormatter> dateTimeFormatters;

    @PostConstruct
    public void init() {
        dateTimeFormatters = patterns.stream().map(DateTimeFormatter::ofPattern).collect(Collectors.toList());
    }

    @Override
    public LocalDateTime convert(String date) {
        for (DateTimeFormatter dateTimeFormatter : dateTimeFormatters) {
            try {
                return LocalDateTime.parse(date, dateTimeFormatter);
            } catch (DateTimeParseException ignore) {
            }
        }
        return null;
    }
}