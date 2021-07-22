/*
 * File: NoneTemplateViewControllerTest.scala
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
import org.junit.jupiter.api.{BeforeEach, DisplayName, Test, TestInfo}
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{content, status}
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

import javax.annotation.Resource
/**
 * The test NoneTemplateViewControllerTest tests the none template excel and pdf views
 *
 * @author T.N.Silverman
 *
 */
@DirtiesContext
@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) class NoneTemplateViewControllerTest extends BaseJpaTest {

  @LocalServerPort private val port : Integer = 0
  @Resource private val webApplicationContext : WebApplicationContext = null
  private var mockMvc : MockMvc = null

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
    this.mockMvc = webAppContextSetup(webApplicationContext).build
  }

  @Test
  @DisplayName("test get pdf view")
  @throws[Exception]
  def whenGetPdfView_ThenStatusOkAndContentTypeIsPdf(): Unit = {
    mockMvc.perform(get("/pdf.htm"))
      .andExpect /*andDo(print()).*/ (status.isOk)
      .andExpect(content.contentType("application/pdf"))
  }

  @Test
  @DisplayName("test get excel view")
  @throws[Exception]
  def whenGetExcelView_ThenStatusOkAndContentTypeIsExcel(): Unit = {
    mockMvc.perform(get("/excel.htm"))
      .andExpect(status.isOk)
      .andExpect(content.contentType("application/vnd.ms-excel"))
  }

  @ParameterizedTest
  @DisplayName("test get page source view")
  @CsvSource(Array("home.html",
    "hello.html",
    "contacts.html",
    "contact.html",
    "contacts.jsp",
    "contact.jsp",
    "hello.jsp",
    "error.html"))
  @throws[Exception]
  def whenGetSourceView_ThenStatusOkAndContentTypeIsText(view: String): Unit = {
    mockMvc.perform(get("/source/" + view))
      .andDo(print)
      .andExpect(status.isOk)
      .andExpect(content.contentType("text/plain;charset=UTF-8"))
  }

  @ParameterizedTest
  @DisplayName("test get non-exit page source view")
  @CsvSource(Array("blah.html",
    "blah.jsp",
    "blah"))
  @throws[Exception]
  def whenGetNonExistSourceView_ThenStatusError(view: String): Unit = {
    mockMvc.perform(get("/source/" + view))
      .andDo(print)
      .andExpect(status.isNotFound)
      .andExpect(content.contentType("text/plain;charset=UTF-8"))
  }

}
