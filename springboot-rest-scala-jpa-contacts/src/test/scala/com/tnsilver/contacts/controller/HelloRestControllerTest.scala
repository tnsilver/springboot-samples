/*
 * File: HelloRestControllerTest.scala
 * Creation Date: Jun 24, 2021
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
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.{BeforeEach, DisplayName, TestInfo}
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.{CsvSource, ValueSource}
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{content, jsonPath, status}
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

import javax.annotation.Resource



/**
 * The test HelloRestControllerTest tests the {@link HelloRestController} rest controller
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest class HelloRestControllerTest extends BaseJpaTest {

  private val restApiEndPoint : String = "/api/"
  private val contentType : String = "application/json;charset=UTF-8"
  @Resource private val webApplicationContext : WebApplicationContext = null
  @Resource private val mappingJackson2HttpMessageConverter : MappingJackson2HttpMessageConverter = null
  private var mockMvc : MockMvc = null

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
    this.mockMvc = webAppContextSetup(webApplicationContext).build
  }

  @ParameterizedTest
  @DisplayName("test hello and greet")
  @ValueSource(strings = Array("hello", "greet"))
  @throws[Exception]
  def whenGetHelloHtmlView_ThenExpectMessageAndStatusOkAndContentTypeJson(path: String): Unit = {
    mockMvc.perform(get(restApiEndPoint + path))
      .andDo(print)
      .andExpect(jsonPath("message", equalTo("Welcome stranger")))
      .andExpect(status.isOk)
      .andExpect(content.contentType(contentType))
  }

  @ParameterizedTest
  @DisplayName("test hello with name")
  @CsvSource(Array("hello,Test", "hello,Tomer"))
  @throws[Exception]
  def whenGetHelloHtmlViewWithName_ThenExpectMessageAndStatusOkAndContentTypeJson(path: String, firstName: String): Unit = {
    mockMvc.perform(get(restApiEndPoint + "/" + path + "/" + firstName))
      .andDo(print)
      .andExpect(jsonPath("message", equalTo("Welcome " + firstName)))
      .andExpect(status.isOk)
      .andExpect(content.contentType(contentType))
  }

  @ParameterizedTest
  @DisplayName("test hello with names")
  @CsvSource(Array("hello,Test,Tester", "hello,Tom,Silverman"))
  @throws[Exception]
  def whenGetHelloHtmlViewWithNames_ThenExpectMessageAndStatusOkAndContentTypeJson(path: String, firstName: String, lastName: String): Unit = {
    mockMvc.perform(get(restApiEndPoint + "/" + path + "/" + firstName + "/" + lastName))
      .andDo(print)
      .andExpect(jsonPath("message", equalTo("Welcome " + firstName + " " + lastName)))
      .andExpect(status.isOk)
      .andExpect(content.contentType(contentType))
  }

}
