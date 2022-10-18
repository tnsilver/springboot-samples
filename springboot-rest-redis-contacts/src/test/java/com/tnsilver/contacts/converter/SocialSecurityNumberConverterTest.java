/*
 * File: SocialSecurityNumberConverterTest.java
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.tnsilver.contacts.base.BaseRedisTest;
import com.tnsilver.contacts.model.SocialSecurityNumber;

/**
 * The test SocialSecurityNumberConverterTest tests the application converter {@link SocialSecurityNumberConverter}
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
// @Transactional
public class SocialSecurityNumberConverterTest extends BaseRedisTest {

    @Resource
    private Environment env;
    @Resource
    private SocialSecurityNumberConverter ssnConverter;

    @ParameterizedTest
    @DisplayName("test good SSN conversion")
    @CsvSource({ "123-45-6789", "100-10-1000" })
    public void whenCreateStringBasedSsn_ThenConverterConvertsTheSame(String ssn) throws Exception {
        SocialSecurityNumber actual = ssnConverter.convert(ssn);
        assertEquals(new SocialSecurityNumber(ssn), actual);
        assertTrue(actual.isValid());
    }

    @ParameterizedTest
    @DisplayName("test malformed SSN conversion")
    @CsvSource({ "123-", "-10-1000" })
    public void whenCreateBadStringSsn_ThenConverterConverts(String ssn) throws Exception {
        assertEquals(new SocialSecurityNumber(ssn), ssnConverter.convert(ssn));
    }

    @ParameterizedTest
    @DisplayName("test numbered SSN conversion")
    @CsvSource({ "123,12,1234,123-12-1234", "123,45,6789,123-45-6789" })
    public void whenCreateBadNumberedSsn_ThenConverterConverts(Integer area, Integer group, Integer serial, String expected)
        throws Exception {
        String ssn = String.format("%s-%s-%s", area, group, serial);
        assertEquals(new SocialSecurityNumber(area, group, serial), ssnConverter.convert(ssn));
    }

    @Test
    @DisplayName("test null SSN conversion")
    public void whenCreateNullSsn_ThenConverterConverts() throws Exception {
        assertEquals(new SocialSecurityNumber(), ssnConverter.convert(null));
    }
}
