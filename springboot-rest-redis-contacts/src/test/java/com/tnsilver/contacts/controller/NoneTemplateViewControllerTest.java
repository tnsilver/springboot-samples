/*
 * File: NoneTemplateViewControllerTest.java
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.tnsilver.contacts.base.BaseRedisTest;

/**
 * The test NoneTemplateViewControllerTest tests the none template excel and pdf views
 *
 * @author T.N.Silverman
 *
 */
@DirtiesContext
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @Transactional
public class NoneTemplateViewControllerTest extends BaseRedisTest {

    @LocalServerPort
    int port;

    // @formatter:off
    @Autowired private WebApplicationContext webApplicationContext;
    // @formatter:on

    private MockMvc mockMvc;

    @BeforeEach
    public void beforeTest(TestInfo info) throws Exception {
        super.beforeEach(info);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("test get pdf view")
    public void whenGetPdfView_ThenStatusOkAndContentTypeIsPdf() throws Exception {
        mockMvc.perform(get("/pdf.htm"))./*andDo(print()).*/andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"));
    }

    @Test
    @DisplayName("test get excel view")
    public void whenGetExcelView_ThenStatusOkAndContentTypeIsExcel() throws Exception {
        mockMvc.perform(get("/excel.htm"))./*andDo(print()).*/andExpect(status().isOk())
            .andExpect(content().contentType("application/vnd.ms-excel"));
    }

    @ParameterizedTest
    @DisplayName("test get page source view")
    @CsvSource({ "home.html", "hello.html", "contacts.html", "contact.html", "contacts.jsp", "contact.jsp", "hello.jsp",
            "error.html" })
    public void whenGetSourceView_ThenStatusOkAndContentTypeIsText(String view) throws Exception {
        mockMvc.perform(get("/source/" + view))./*andDo(print()).*/andExpect(status().isOk())
            .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @ParameterizedTest
    @DisplayName("test get non-exit page source view")
    @CsvSource({ "blah.html", "blah.jsp", "blah" })
    public void whenGetNonExistSourceView_ThenStatusError(String view) throws Exception {
        mockMvc.perform(get("/source/" + view))./*andDo(print()).*/andExpect(status().isNotFound())
            .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }
}
