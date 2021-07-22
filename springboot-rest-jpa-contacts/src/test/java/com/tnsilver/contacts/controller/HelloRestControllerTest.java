/*
 * File: HelloRestControllerTest.java
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
package com.tnsilver.contacts.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.tnsilver.contacts.base.BaseJpaTest;
import com.tnsilver.contacts.model.Contact;

/**
 * The test HelloRestControllerTest tests the {@link HelloRestController} rest controller
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
// @Transactional
public class HelloRestControllerTest extends BaseJpaTest {

    private static final String restApiEndPoint = "/api/";
    private String contentType = "application/json;charset=UTF-8";
    @Resource
    private Environment env;
    @Resource
    private WebApplicationContext webApplicationContext;
    @Resource
    private HttpMessageConverter<Contact> mappingJackson2HttpMessageConverter;
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeTest(TestInfo info) throws Exception {
        super.beforeEach(info);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @DisplayName("test hello and greet")
    @ValueSource(strings = { "hello", "greet" })
    public void whenGetHelloHtmlView_ThenExpectMessageAndStatusOkAndContentTypeJson(String path) throws Exception {
        mockMvc.perform(get(restApiEndPoint + path)).andDo(print()).andExpect(jsonPath("message", equalTo("Welcome stranger")))
            .andExpect(status().isOk()).andExpect(content().contentType(contentType));
    }

    @ParameterizedTest
    @DisplayName("test hello with name")
    @CsvSource({"hello,Test","hello,Tomer"})
    public void whenGetHelloHtmlViewWithName_ThenExpectMessageAndStatusOkAndContentTypeJson(String path, String firstName) throws Exception {
        mockMvc.perform(get(restApiEndPoint + "/" + path + "/" + firstName)).andDo(print())
            .andExpect(jsonPath("message", equalTo("Welcome " + firstName))).andExpect(status().isOk())
            .andExpect(content().contentType(contentType));
    }

    @ParameterizedTest
    @DisplayName("test hello with names")
    @CsvSource({"hello,Test,Tester","hello,Tom,Silverman"})
    public void whenGetHelloHtmlViewWithNames_ThenExpectMessageAndStatusOkAndContentTypeJson(String path, String firstName, String lastName) throws Exception {
        mockMvc.perform(get(restApiEndPoint + "/" + path + "/" + firstName + "/" + lastName)).andDo(print())
            .andExpect(jsonPath("message", equalTo("Welcome " + firstName + " " + lastName))).andExpect(status().isOk())
            .andExpect(content().contentType(contentType));
    }

}
