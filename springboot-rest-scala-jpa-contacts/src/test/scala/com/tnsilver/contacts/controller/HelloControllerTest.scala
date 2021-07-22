/*
 * File: HelloControllerTest.scala
 * Creation Date: Jun 25, 2021
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
package com.tnsilver.contacts.controller

import com.tnsilver.contacts.base.BaseJpaTest
import com.tnsilver.contacts.config.WebConfig
import org.junit.jupiter.api.{BeforeEach, DisplayName, Test, TestInfo}
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{content, forwardedUrl, status}
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

import javax.annotation.Resource



/**
 * The test HelloControllerTest tests the HTML {@code /html/hello} and JSP {@code /jsp/hello} automatic controllers
 * defined in
 * {@link WebConfig# addViewControllers ( org.springframework.web.servlet.config.annotation.ViewControllerRegistry )}
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // @Transactional
class HelloControllerTest extends BaseJpaTest {

  @LocalServerPort private val port : Integer = 0
  @Resource private val webApplicationContext : WebApplicationContext = null
  private var mockMvc : MockMvc = null

  @BeforeEach
  @throws[Exception]
  def beforeTest(info: TestInfo): Unit = {
    super.beforeEach(info)
    this.mockMvc = webAppContextSetup(webApplicationContext).build
  }

  @Test
  @DisplayName("test get hello.html view")
  @throws[Exception]
  def whenGetHtmlHello_ThenStatusOkAndContentTypeText(): Unit = {
    mockMvc.perform(get("/html/hello"))
      .andDo(print)
      .andExpect(status.isOk)
      .andExpect(content.contentType("text/html;charset=UTF-8"))
  }

  @Test
  @DisplayName("test forward to hello.jsp view")
  @throws[Exception]
  def whenGetJspHello_ThenStatusOkAndExpectedForwardedUrl(): Unit = {
    mockMvc.perform(get("/jsp/hello"))
      .andDo(print)
      .andExpect(status.isOk)
      .andExpect(forwardedUrl("/WEB-INF/views/jsp/hello.jsp"))
  }
}
