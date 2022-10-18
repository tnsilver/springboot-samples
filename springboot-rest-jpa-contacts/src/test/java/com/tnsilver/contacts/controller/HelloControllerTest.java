/*
 * File: HelloControllerTest.java
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.tnsilver.contacts.base.BaseJpaTest;
import com.tnsilver.contacts.config.WebConfig;

/**
 * The test HelloControllerTest tests the HTML {@code /html/hello} and JSP {@code /jsp/hello} automatic controllers
 * defined in
 * {@link WebConfig#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)}
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @Transactional
public class HelloControllerTest extends BaseJpaTest {

    @LocalServerPort
    int port;
    @Resource
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeTest(TestInfo info) throws Exception {
        super.beforeEach(info);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("test get hello.html view")
    public void whenGetHtmlHello_ThenStatusOkAndContentTypeText() throws Exception {
        mockMvc.perform(get("/html/hello")).andDo(print()).andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @DisplayName("test forward to hello.jsp view")
    public void whenGetJspHello_ThenStatusOkAndExpectedForwardedUrl() throws Exception {
        mockMvc.perform(get("/jsp/hello")).andDo(print()).andExpect(status().isOk())
            .andExpect(forwardedUrl("/WEB-INF/views/jsp/hello.jsp"));
    }
}
