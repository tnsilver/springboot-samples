/*
 * File: ExceptionsTest.java
 * Creation Date: Jul 20, 2021
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
package com.tnsilver.contacts.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

import com.tnsilver.contacts.base.BaseJpaTest;

/**
 * The test ExceptionsTest tests the application exceptions {@link TeapotException} and {@link PaymentRequiredException}
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
public class ExceptionsTest extends BaseJpaTest{

    @ParameterizedTest
    @DisplayName("test create teapot exception")
    @CsvSource("error,cause")
    public void whenConstructTeapotException_ThenConstructed(String message, String cause) throws Exception {
        // @formatter:off
        assertAll(() -> assertNotNull(new TeapotException()),
                  () -> assertNotNull(new TeapotException(message)),
                  () -> assertEquals(new TeapotException(message).getMessage(), message),
                  () -> assertNotNull(new TeapotException(message, new Exception(cause))),
                  () -> assertEquals(new TeapotException(message, new Exception(cause)).getCause().getMessage(), cause));
        // @formatter:on
    }

    @ParameterizedTest
    @DisplayName("test create payment exception")
    @CsvSource("error,cause")
    public void whenConstructPaymentException_ThenConstructed(String message, String cause) throws Exception {
        // @formatter:off
        assertAll(() -> assertNotNull(new PaymentRequiredException()),
                  () -> assertNotNull(new PaymentRequiredException(message)),
                  () -> assertEquals(new PaymentRequiredException(message).getMessage(), message),
                  () -> assertNotNull(new PaymentRequiredException(message, new Exception(cause))),
                  () -> assertEquals(new PaymentRequiredException(message, new Exception(cause)).getCause().getMessage(),cause));
        // @formatter:on
    }
}
