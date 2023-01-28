/*
 * File: RestTemplateApiTest.java
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
package com.tnsilver.contacts.rest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resources;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tnsilver.contacts.base.BaseJpaTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.SocialSecurityNumber;

/**
 * The test RestTemplateIT tests the REST api of the application using a rest template
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class RestTemplateApiTest extends BaseJpaTest {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateApiTest.class);
    @LocalServerPort int port;
    @Autowired ObjectMapper mapper;
    @Autowired private TestRestTemplate restTemplate;
    // configured fields
    private String restApiEndPoint;
    private Contact homer;
    private Contact tester;

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseJpaTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
        restApiEndPoint = "http://localhost:" + port + "/api/contacts";
        homer = new Contact(new SocialSecurityNumber("123-45-6789"), "Homer", "Simpson", LocalDate.parse("1978-06-20"), Boolean.TRUE,
            3);
        tester = new Contact(new SocialSecurityNumber("987-65-4321"), "Test", "Tester", LocalDate.now(), Boolean.FALSE, 0);
    }

    /** RestTemplate GET requests: with a quick example using the getForEntity() API */
    @Test
    @DisplayName("test get for contact entity")
    public void whenGetForEntity_ThenGetExpectedNumOfElementsAndStatus() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(restApiEndPoint, String.class);
        assertThat(response, not(nullValue()));
        assertThat(response.getBody(), not(emptyString()));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        int size = JsonPath.read(response.getBody(), "$.page.totalElements");
        assertThat(size, is(NUM_OF_CONTACTS));
    }

    /** map the response directly to a Resource DTO with the getForObject API */
    @Test
    @DisplayName("test get for contact")
    public void whenQueryForObject_ThenReturnValidContact() throws Exception {
        Contact expected = restTemplate.getForObject(restApiEndPoint + "/1", Contact.class);
        // @formatter:off
        assertAll(() -> assertThat(expected, notNullValue()),
                  () -> assertThat(expected.getId(), notNullValue()),
                  () -> assertThat(expected.getFirstName(), equalTo(homer.getFirstName())),
                  () -> assertThat(expected.getLastName(), equalTo(homer.getLastName())),
                  () -> assertThat(expected.getSsn(), equalTo(homer.getSsn())),
                  () -> assertThat(expected.getBirthDate(), equalTo(homer.getBirthDate())),
                  () -> assertThat(expected.getMarried(), equalTo(homer.getMarried())),
                  () -> assertThat(expected.getChildren(), equalTo(homer.getChildren())));
       // @formatter:on
    }

    @SuppressWarnings("unused")
    private <T> List<T> exchangeForList(TestRestTemplate restTemplate, String url, String startNodeName) throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.getBody());
        String json = jsonNode.at("/_embedded/" + startNodeName).toString();
        return mapper.readValue(json, new TypeReference<List<T>>() {});
    }

    /**
     * A workaround the HAL standard '_embedded' node in json response. We use {@link Resources} to wrap our entities
     * and use the RestTemplate getForObject API. Contact entities are now maps inside the Resources .getContent().
     */
    @Test
    @DisplayName("test exchange for contacts list")
    public void whenExchangeForStringResponse_ThenGetExpectedContactsInResponse() throws Exception {
        //List<Contact> contacts = exchangeForList(restTemplate, restApiEndPoint + "?size=1000", "contacts");
        ResponseEntity<String> response = restTemplate.exchange(restApiEndPoint + "?size=" + NUM_OF_CONTACTS, HttpMethod.GET, null, String.class);
        JsonNode jsonNode = mapper.readTree(response.getBody());
        String json = jsonNode.at("/_embedded/contacts").toString();
        List<Contact> contacts = mapper.readValue(json, new TypeReference<List<Contact>>() {});
        assertThat(contacts, notNullValue());
        assertThat(contacts.size(), equalTo(NUM_OF_CONTACTS));
    }

    /** using an OPTIONS request and exploring the allowed operations on a specific URI using the optionsForAllow API */
    @Test
    @DisplayName("test contacts API allows GET, HEAD, POST, OPTIONS")
    public void whenOptionsContacts_ThenAllowGetHeadPostAndOptions() throws Exception {
        Set<HttpMethod> optionsForAllow = restTemplate.optionsForAllow(restApiEndPoint);
        // @formatter:off
        assertThat(optionsForAllow, containsInAnyOrder(HttpMethod.GET,
                                                       HttpMethod.HEAD,
                                                       HttpMethod.POST,
                                                       HttpMethod.OPTIONS));
        // @formatter:on
    }

    /** using an OPTIONS request and exploring the allowed operations on a specific URI using the optionsForAllow API */
    @Test
    @DisplayName("test contact API allows all Http methods")
    public void whenOptionsContact_ThenAllowsHttpMethods() throws Exception {
        Set<HttpMethod> optionsForAllow = restTemplate.optionsForAllow(restApiEndPoint + "/1");
        // @formatter:off
        assertThat(optionsForAllow, containsInAnyOrder(HttpMethod.GET,
                                                       HttpMethod.HEAD,
                                                       HttpMethod.PUT,
                                                       HttpMethod.PATCH,
                                                       HttpMethod.DELETE,
                                                       HttpMethod.OPTIONS));
        // @formatter:on
    }

    /** Doing a HEAD request by using the headForHeaders() API */
    @Test
    @DisplayName("test HEAD includes Connection header")
    public void whenHeaders_ThenConnectionKeepAlive() throws Exception {
        /*HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);*/
        HttpHeaders httpHeaders = restTemplate.headForHeaders(restApiEndPoint + "/1");
        httpHeaders.entrySet().forEach(e -> logger.debug("{} -> {}", e.getKey(), e.getValue()));
        assertThat(httpHeaders.getConnection(), contains("keep-alive"));
    }

    /**
     * Using postForLocation API -> instead of returning the full Resource, just returns the Location of that newly
     * created Resource:
     */
    @Test
    @DisplayName("test post for location")
    public void whenPostForLocation_ThenSaveDocumentAndLocationIsValid() throws Exception {
        HttpEntity<Contact> request = new HttpEntity<>(tester);
        URI location = restTemplate.postForLocation(restApiEndPoint, request);
        assertThat(location, notNullValue());
        assertThat(location.toString(), containsStringIgnoringCase(restApiEndPoint));
        String id = location.toString().split(restApiEndPoint + "/")[1];
        assertThat(id, notNullValue());
        restTemplate.delete(location);
    }

    /**
     * In order to create a new Resource in the API – we can make use of the postForLocation(), postForObject() or
     * postForEntity() APIs.
     * <p>
     * The postForLocation returns the URI of the newly created Resource while the second returns the Resource itself.
     */
    @Test
    @DisplayName("test post for contact")
    public void whenPostContact_ThenIdIsNotNullAndPropertiesAreExpected() throws Exception {
        Contact expected = new Contact(new SocialSecurityNumber("123-45-6789"), "Test", "Tester", LocalDate.parse("1980-02-06"), true,
            3);
        HttpEntity<Contact> request = new HttpEntity<>(expected);
        Contact actual = restTemplate.postForObject(restApiEndPoint, request, Contact.class);
        // @formatter:off
        assertAll(() -> assertThat(actual, notNullValue()),
                  () -> assertThat(actual.getId(), notNullValue()),
                  () -> assertThat(actual.getSsn().getSsn(), is(expected.getSsn().getSsn())),
                  () -> assertThat(actual.getFirstName(), is(expected.getFirstName())),
                  () -> assertThat(actual.getLastName(), is(expected.getLastName())),
                  () -> assertThat(actual.getBirthDate(), is(expected.getBirthDate())),
                  () -> assertThat(actual.getMarried(), is(expected.getMarried())),
                  () -> assertThat(actual.getChildren(), is(expected.getChildren())));
        // @formatter:on
        restTemplate.delete(restApiEndPoint + "/" + actual.getId());
    }

    /** doing a POST with the more generic exchange API and using patchForObject to update an entity */
    @Test
    @DisplayName("test patch for contact")
    public void whenPatchContact_ThenContactIsUpdated() throws Exception {
        // insert the contact and get the location
        URI location = restTemplate.postForLocation(restApiEndPoint, new HttpEntity<>(tester));
        // @formatter:off
        String json = "{\"ssn\": \"987-65-4321\",\"firstName\": \"Tset\", \"lastName\": \"Retset\","
                    + "\"birthDate\": \"1980-06-20T00:00:00Z\",\"married\": false,\"children\": 0}";
        // create patch request entity with content-type header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(json,headers);
        // patch (update) the location with the json data
        restTemplate.patchForObject(location, entity, Void.class);
        // retrieve the contact and test the update
        Contact actual = restTemplate.getForObject(location, Contact.class);
        logger.debug("AFTER PATCH: {}",actual);
        assertThat(actual.getId(), notNullValue());
        assertAll(() -> assertThat(actual.getSsn(), is(tester.getSsn())),
                  () -> assertThat(actual.getFirstName(), is("Tset")),
                  () -> assertThat(actual.getLastName(), is("Retset")),
                  () -> assertThat(actual.getBirthDate(), is(LocalDate.parse("1980-06-20"))),
                  () -> assertThat(actual.getMarried(), is(tester.getMarried())),
                  () -> assertThat(actual.getChildren(), is(tester.getChildren())));
        // @formatter:on
        restTemplate.delete(location);
    }

    /** The template throws an exception if the resource is not found */
    @Test
    @DisplayName("test get non-exists and get exception")
    public void whenGetForNonExistingContact_ThenHttpClientError() throws Exception {
        ResponseEntity<Contact> response = restTemplate.getForEntity(restApiEndPoint + "/0", Contact.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    /** remove an existing Resource with the delete() API: */
    @Test
    @DisplayName("test delete contact")
    public void whenDeleteContact_ThenGetForEntityIsNotFound() throws Exception {
        // @formatter:off
        HttpEntity<Contact> entity = new HttpEntity<>(tester);
        URI location = restTemplate.postForLocation(restApiEndPoint, entity);
        assertThat(location, notNullValue());
        restTemplate.delete(location);
        ResponseEntity<Contact> response = restTemplate.getForEntity(location, Contact.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        // @formatter:off
    }
}
