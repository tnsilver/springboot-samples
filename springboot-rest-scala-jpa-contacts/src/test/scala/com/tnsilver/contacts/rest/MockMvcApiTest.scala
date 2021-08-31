/*
 * File: MockMvcApiTest.java
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
package com.tnsilver.contacts.rest

import com.jayway.jsonpath.JsonPath
import com.tnsilver.contacts.base.BaseJpaTest
import com.tnsilver.contacts.model.{Contact, SocialSecurityNumber}
import org.hamcrest.Matchers.{equalTo, hasSize}
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api._
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.http.MockHttpOutputMessage
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{content, jsonPath, status}
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

import java.io.IOException
import java.time.LocalDate
import javax.annotation.Resource
import javax.transaction.Transactional
/**
 * The test RestAPIMockIT tests the REST api of this application using a mock mvc object
 *
 * @author T.N.Silverman
 *
 */

@SpringBootTest
@Transactional class MockMvcApiTest extends BaseJpaTest {

  private val restApiEndPoint = "/api/contacts"
  private val contentType = "application/hal+json"
  @Resource private var webApplicationContext : WebApplicationContext = null
  @Resource private var mappingJackson2HttpMessageConverter : MappingJackson2HttpMessageConverter = null

  private var mockMvc : MockMvc = null

  @BeforeAll
  @throws[Exception]
  override def beforeAll(): Unit = {
    super.beforeAll()
  }

  @BeforeEach
  @throws[Exception]
  def beforeTest(info: TestInfo): Unit = {
    super.beforeEach(info)
    this.mockMvc = webAppContextSetup(webApplicationContext).build
  }

  /**
   * perform a get to the rest api end point and expect a response containing 20 (default page size) contacts, status
   * 200 and content type application/hal+json;charset=UTF-8
   */
  @Test
  @DisplayName("test get contacts")
  @throws[Exception]
  def whenGetContacts_ThenCorrectStatusAndContentTypeAndSize(): Unit = { // @formatter:off
    mockMvc.perform(get(restApiEndPoint).param("size", "34")).andDo(print).andExpect(jsonPath("_embedded.contacts", hasSize(34))).andExpect(status.isOk).andExpect(content.contentType(contentType))
    // @formatter:on
  }

  /**
   * perform a get to the rest api end point and expect a response containing 1 contact (Homer Simpson), status 200
   * and content type application/hal+json;charset=UTF-8
   */
  @Test
  @DisplayName("test get contact")
  @throws[Exception]
  def whenGetContactById_ThenCorrectValuesAndStatusAndContentType(): Unit = {
    mockMvc.perform(get(restApiEndPoint + "/1")).andDo(print).andExpect(status.isOk).andExpect(jsonPath("$.firstName", equalTo("Homer"))).andExpect(jsonPath("$.lastName", equalTo("Simpson"))).andExpect(jsonPath("$.birthDate", equalTo("1978-06-20"))).andExpect(jsonPath("$.married", equalTo(true))).andExpect(jsonPath("$.children", equalTo(3))).andExpect(content.contentType(contentType))
  }

  /**
   * perform a post to the rest api end point supplying a contact json and expect a status 201 and content type
   * application/hal+json;charset=UTF-8 with the created contact in the response body
   */
  @Test
  @DisplayName("test post contact")
  @throws[Exception]
  def whenPostContact_ThenCorrectValuesAndStatus(): Unit = {
    val contact = new Contact(new SocialSecurityNumber("123-45-6789"), "Test", "Contact", LocalDate.parse("1980-02-06"), java.lang.Boolean.TRUE, 3)
    val json = toJsonString(contact)
    logger.debug("saving contact ", json)
    // @formatter:off
    val result = mockMvc.perform(post(restApiEndPoint)
      .contentType(contentType)
      .content(json))
      .andDo(print)
      .andExpect(status.isCreated)
      .andExpect(jsonPath("$.firstName", equalTo("Test")))
      .andExpect(jsonPath("$.lastName", equalTo("Contact")))
      .andReturn
    val id : Int = JsonPath.read(result.getResponse.getContentAsString, "$.id")
    assertNotNull(id)
    mockMvc.perform(delete(restApiEndPoint + "/" + id)
      .contentType(contentType)
      .content(json))
      .andDo(print)
      .andExpect(status.isNoContent)
  }

  @Test
  @DisplayName("test put contact")
  @throws[Exception]
  def whenPutContact_ThenCorrectValuesAndStatus(): Unit = {
    val id = Integer.valueOf(1)
    val contact = new Contact(new SocialSecurityNumber("123-45-6789"), "Homer", "Simpson", LocalDate.parse("1978-06-20"), java.lang.Boolean.FALSE, 0)
    var json = toJsonString(contact)
    logger.debug("putting contact ", json)
    mockMvc.perform(put(restApiEndPoint + "/" + id).contentType(contentType).content(json)).andDo(print).andExpect(status.isOk).andExpect(jsonPath("$.married", equalTo(false))).andExpect(jsonPath("$.children", equalTo(0)))
    contact.setMarried(true)
    contact.setChildren(3)
    json = toJsonString(contact)
    mockMvc.perform(put(restApiEndPoint + "/" + id).contentType(contentType).content(json)).andDo(print).andExpect(status.isOk).andExpect(jsonPath("$.married", equalTo(true))).andExpect(jsonPath("$.children", equalTo(3)))
  }

  /**
   * perform a patch to the rest api end point supplying a contact json and expect a status 200 and content type
   * application/hal+json;charset=UTF-8 with the updated contact in the response body
   */
  @Test
  @DisplayName("test patch contact")
  @throws[Exception]
  def whenPatchContact_ThenCorrectValuesAndStatusAndContentType(): Unit = {
    var homer = "{\"married\" : false, \"children\" : 0}"
    mockMvc.perform(patch(restApiEndPoint + "/1").contentType(contentType).content(homer)).andDo(print).andExpect(status.isOk).andExpect(jsonPath("$.married", equalTo(false))).andExpect(jsonPath("$.children", equalTo(0))).andExpect(content.contentType(contentType))
    homer = "{\"married\" : true, \"children\" : 3}"
    mockMvc.perform(patch(restApiEndPoint + "/1").contentType(contentType).content(homer)).andDo(print).andExpect(status.isOk).andExpect(jsonPath("$.married", equalTo(true))).andExpect(jsonPath("$.children", equalTo(3))).andExpect(content.contentType(contentType))
  }

  /** perform a get to the end point with a none existent resource and expect a status 404 - Not Found */
  @Test
  @DisplayName("test get none exising contact")
  @throws[Exception]
  def whenGetNoneExistingContact_ThenStatus404(): Unit = {
    mockMvc.perform(get(restApiEndPoint + "/0")).andDo(print).andExpect(status.isNotFound)
  }

  /**
   * Utility to convert Contact objects to json string
   *
   * @param contact the contact object
   * @return a json representation of the contact object
   * @throws IOException if anything goes kaput
   */
  @throws[IOException]
  def toJsonString(contact: Contact): String  = {
    val mockHttpOutputMessage = new MockHttpOutputMessage
    this.mappingJackson2HttpMessageConverter.write(contact, MediaType.APPLICATION_JSON, mockHttpOutputMessage)
    mockHttpOutputMessage.getBodyAsString
  }
}
