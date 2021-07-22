/*
 * File: HelloRestControllerTest.java
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
package com.tnsilver.contacts.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.tnsilver.contacts.base.BaseRedisTest;

/**
 * The test HelloRestControllerTest tests the {@link HelloRestController} rest controller
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
// @Transactional
public class HelloRestControllerTest extends BaseRedisTest {

    private static final String restApiEndPoint = "/api/";
    private String contentType = "application/json;charset=UTF-8";
    // @formatter:off
    @Autowired private WebApplicationContext webApplicationContext;
    // @formatter:on
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeTest(TestInfo info) throws Exception {
        super.beforeEach(info);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @DisplayName("test hello and greet")
    @CsvSource({ "hello,system", "greet,stranger" })
    public void whenGetHelloHtmlView_ThenExpectMessageAndStatusOkAndContentTypeJson(String path, String expected)
        throws Exception {
        String message = "Welcome " + expected;
        mockMvc.perform(get(restApiEndPoint + path))
               /*.andDo(print()).*/
               .andExpect(jsonPath("message", equalTo(message)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType));
    }

    @ParameterizedTest
    @DisplayName("test hello with name")
    @CsvSource({ "hello,Test", "hello,Tomer" })
    public void whenGetHelloHtmlViewWithName_ThenExpectMessageAndStatusOkAndContentTypeJson(String path,
        String firstName) throws Exception {
        mockMvc.perform(get(restApiEndPoint + "/" + path + "/" + firstName))
               /*.andDo(print())*/
               .andExpect(jsonPath("message", equalTo("Welcome " + firstName)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType));
    }

    @ParameterizedTest
    @DisplayName("test hello with names")
    @CsvSource({ "hello,Test,Tester", "hello,Tom,Silverman" })
    public void whenGetHelloHtmlViewWithNames_ThenExpectMessageAndStatusOkAndContentTypeJson(String path,
        String firstName, String lastName) throws Exception {
        mockMvc.perform(get(restApiEndPoint + "/" + path + "/" + firstName + "/" + lastName))
               /*.andDo(print())*/
               .andExpect(jsonPath("message", equalTo("Welcome " + firstName + " " + lastName)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType));
    }
}
