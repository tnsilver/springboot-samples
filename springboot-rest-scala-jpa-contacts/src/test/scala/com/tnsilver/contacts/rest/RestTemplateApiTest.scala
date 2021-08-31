/*
 * File: RestTemplateApiTest.scala
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
package com.tnsilver.contacts.rest

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.jayway.jsonpath.JsonPath
import com.tnsilver.contacts.base.BaseJpaTest
import com.tnsilver.contacts.model.{Contact, SocialSecurityNumber}
import org.hamcrest.CoreMatchers.{notNullValue, nullValue}
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers._
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http._

import java.net.URI
import java.time.LocalDate
import javax.annotation.Resources
import javax.transaction.Transactional
/**
 * The test RestTemplateIT tests the REST api of the application using a rest template
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional class RestTemplateApiTest extends BaseJpaTest {

  @LocalServerPort private val port = 0
  @Autowired  val mapper : ObjectMapper = null
  @Autowired val restTemplate : TestRestTemplate = null
  // configured fields
  private var restApiEndPoint : String = null
  private var homer : Contact= null
  private var tester : Contact = null

  @BeforeAll
  @throws[Exception]
  override def beforeAll(): Unit = {
    super.beforeAll()
  }

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
    restApiEndPoint = "http://localhost:" + port + "/api/contacts"
    homer = new Contact(new SocialSecurityNumber("123-45-6789"), "Homer", "Simpson", LocalDate.parse("1978-06-20"), java.lang.Boolean.TRUE, 3)
    tester = new Contact(new SocialSecurityNumber("987-65-4321"), "Test", "Tester", LocalDate.now, java.lang.Boolean.FALSE, 0)
  }

  /** RestTemplate GET requests: with a quick example using the getForEntity() API */
  @Test
  @DisplayName("test get for contact entity")
  @throws[Exception]
  def whenGetForEntity_ThenGetExpectedNumOfElementsAndStatus(): Unit = {
    val response : ResponseEntity[String] = restTemplate.getForEntity(restApiEndPoint, classOf[String])
    assertThat(response, not(nullValue))
    assertThat(response.getBody, not(emptyString))
    assertThat(response.getStatusCode, equalTo(HttpStatus.OK))
    val size : Int = JsonPath.read(response.getBody, "$.page.totalElements")
    assertEquals(NUM_OF_CONTACTS, size)
  }

  /** map the response directly to a Resource DTO with the getForObject API */
  @Test
  @DisplayName("test get for contact")
  @throws[Exception]
  def whenQueryForObject_ThenReturnValidContact(): Unit = {
    val expected : Contact = restTemplate.getForObject(restApiEndPoint + "/1", classOf[Contact])
    // @formatter:off
    assertAll(
      () => assertThat(expected, notNullValue),
      () => assertThat(expected.getId, notNullValue),
      () => assertThat(expected.getFirstName, equalTo(homer.getFirstName)),
      () => assertThat(expected.getLastName, equalTo(homer.getLastName)),
      () => assertThat(expected.getSsn, equalTo(homer.getSsn)),
      () => assertThat(expected.getBirthDate, equalTo(homer.getBirthDate)),
      () => assertThat(expected.getMarried, equalTo(homer.getMarried)),
      () => assertThat(expected.getChildren, equalTo(homer.getChildren))
    )
    // @formatter:on
  }

  /**
   * A workaround the HAL standard '_embedded' node in json response. We use {@link Resources} to wrap our entities
   * and use the RestTemplate getForObject API. Contact entities are now maps inside the Resources .getContent().
   */
  @Test
  @DisplayName("test exchange for contacts list")
  @throws[Exception]
  def whenExchangeForStringResponse_ThenGetExpectedContactsInResponse(): Unit = {
    val response : ResponseEntity[String] = restTemplate.exchange(restApiEndPoint + "?size=" + NUM_OF_CONTACTS, HttpMethod.GET, null, classOf[String])
    val jsonNode : JsonNode = mapper.readTree(response.getBody)
    val json : String = jsonNode.at("/_embedded/contacts").toString
    val contacts : java.util.List[Contact] = mapper.readValue(json, new TypeReference[java.util.List[Contact]]() {})
    assertThat(contacts, notNullValue)
    assertEquals(NUM_OF_CONTACTS,contacts.size())
  }

  /** using an OPTIONS request and exploring the allowed operations on a specific URI using the optionsForAllow API */
  @Test
  @DisplayName("test contacts API allows GET, HEAD, POST, OPTIONS")
  @throws[Exception]
  def whenOptionsContacts_ThenAllowGetHeadPostAndOptions(): Unit = {
    val optionsForAllow : java.util.Set[HttpMethod] = restTemplate.optionsForAllow(restApiEndPoint)
    assertThat(optionsForAllow, containsInAnyOrder(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.POST, HttpMethod.OPTIONS))
  }

  @Test
  @DisplayName("test contact API allows all Http methods")
  @throws[Exception]
  def whenOptionsContact_ThenAllowsHttpMethods(): Unit = {
    val optionsForAllow : java.util.Set[HttpMethod] = restTemplate.optionsForAllow(restApiEndPoint + "/1")
    assertThat(optionsForAllow, containsInAnyOrder(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.OPTIONS))
  }

  /** Doing a HEAD request by using the headForHeaders() API */
  @Test
  @DisplayName("test HEAD includes Connection header")
  @throws[Exception]
  def whenHeaders_ThenConnectionKeepAlive(): Unit = {
    val httpHeaders : HttpHeaders = restTemplate.headForHeaders(restApiEndPoint + "/1")
    httpHeaders.entrySet.forEach((e) => logger.info("{} -> {}", e.getKey, e.getValue))
    assertThat(httpHeaders.getConnection, contains("keep-alive"))
  }

  /**
   * Using postForLocation API -> instead of returning the full Resource, just returns the Location of that newly
   * created Resource:
   */
  @Test
  @DisplayName("test post for location")
  @throws[Exception]
  def whenPostForLocation_ThenSaveDocumentAndLocationIsValid(): Unit = {
    val request : HttpEntity[Contact] = new HttpEntity[Contact](tester)
    val location : URI = restTemplate.postForLocation(restApiEndPoint, request)
    assertThat(location, notNullValue)
    assertThat(location.toString, containsStringIgnoringCase(restApiEndPoint))
    val id : String = location.toString.split(restApiEndPoint + "/")(1)
    assertThat(id, notNullValue)
    restTemplate.delete(location)
  }

  /**
   * In order to create a new Resource in the API – we can make use of the postForLocation(), postForObject() or
   * postForEntity() APIs.
   * <p>
   * The postForLocation returns the URI of the newly created Resource while the second returns the Resource itself.
   */
  @Test
  @DisplayName("test post for contact")
  @throws[Exception]
  def whenPostContact_ThenIdIsNotNullAndPropertiesAreExpected(): Unit = {
    val expected : Contact = new Contact(new SocialSecurityNumber("123-45-6789"), "Test", "Tester", LocalDate.parse("1980-02-06"), true, 3)
    val request : HttpEntity[Contact] = new HttpEntity[Contact](expected)
    val actual : Contact = restTemplate.postForObject(restApiEndPoint, request, classOf[Contact])
    assertAll(
      () => assertThat(actual, notNullValue),
      () => assertThat(actual.getId, notNullValue),
      () => assertThat(actual.getSsn.getSsn, is(expected.getSsn.getSsn)),
      () => assertThat(actual.getFirstName, is(expected.getFirstName)),
      () => assertThat(actual.getLastName, is(expected.getLastName)),
      () => assertThat(actual.getBirthDate, is(expected.getBirthDate)),
      () => assertThat(actual.getMarried, is(expected.getMarried)),
      () => assertThat(actual.getChildren, is(expected.getChildren))
    )
    restTemplate.delete(restApiEndPoint + "/" + actual.getId)
  }

  /** doing a POST with the more generic exchange API and using patchForObject to update an entity */
  @Test
  @DisplayName("test patch for contact")
  @throws[Exception]
  def whenPatchContact_ThenContactIsUpdated(): Unit = { // insert the contact and get the location
    val location : URI = restTemplate.postForLocation(restApiEndPoint, new HttpEntity[Contact](tester))
    val json : String = "{\"ssn\": \"987-65-4321\",\"firstName\": \"Tset\", \"lastName\": \"Retset\"," + "\"birthDate\": \"1980-06-20T00:00:00Z\",\"married\": false,\"children\": 0}"
    // create patch request entity with content-type header
    val headers : HttpHeaders = new HttpHeaders()
    headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)
    val entity : HttpEntity[String] = new HttpEntity[String](json, headers)
    // patch (update) the location with the json data
    restTemplate.patchForObject(location, entity, classOf[Void])
    // retrieve the contact and test the update
    val actual : Contact = restTemplate.getForObject(location, classOf[Contact])
    logger.info("AFTER PATCH: {}", actual)
    assertThat(actual.getId, notNullValue)
    assertAll(
      () => assertThat(actual.getSsn, is(tester.getSsn)),
      () => assertThat(actual.getFirstName, is("Tset")),
      () => assertThat(actual.getLastName, is("Retset")),
      () => assertThat(actual.getBirthDate, is(LocalDate.parse("1980-06-20"))),
      () => assertThat(actual.getMarried, is(tester.getMarried)),
      () => assertThat(actual.getChildren, is(tester.getChildren))
    )
    restTemplate.delete(location)
  }

  /** The template throws an exception if the resource is not found */
  @Test
  @DisplayName("test get non-exists and get exception")
  @throws[Exception]
  def whenGetForNonExistingContact_ThenHttpClientError(): Unit = {
    val response : ResponseEntity[Contact] = restTemplate.getForEntity(restApiEndPoint + "/0", classOf[Contact])
    assertThat(response.getStatusCode, equalTo(HttpStatus.NOT_FOUND))
  }

  /** remove an existing Resource with the delete() API: */
  @Test
  @DisplayName("test delete contact")
  @throws[Exception]
  def whenDeleteContact_ThenGetForEntityIsNotFound(): Unit = {
    val entity : HttpEntity[Contact] = new HttpEntity[Contact](tester)
    val location : URI = restTemplate.postForLocation(restApiEndPoint, entity)
    assertThat(location, notNullValue)
    restTemplate.delete(location)
    val response : ResponseEntity[Contact] = restTemplate.getForEntity(location, classOf[Contact])
    assertThat(response.getStatusCode, equalTo(HttpStatus.NOT_FOUND))
  }
}
