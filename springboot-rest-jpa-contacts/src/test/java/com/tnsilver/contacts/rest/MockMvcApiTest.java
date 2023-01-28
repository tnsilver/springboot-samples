/*
 * File: MockMvcApiTest.java
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;
import com.tnsilver.contacts.base.BaseJpaTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.SocialSecurityNumber;

import jakarta.transaction.Transactional;

/**
 * The test RestAPIMockIT tests the REST api of this application using a mock mvc object
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
@Transactional
public class MockMvcApiTest extends BaseJpaTest {

    private static final Logger logger = LoggerFactory.getLogger(MockMvcApiTest.class);
    private static final String restApiEndPoint = "/api/contacts";
    private String contentType = "application/hal+json";

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private HttpMessageConverter<Contact> mappingJackson2HttpMessageConverter;
    private MockMvc mockMvc;

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseJpaTest.beforeAll();
    }

    @BeforeEach
    public void beforeTest(TestInfo info) throws Exception {
        super.beforeEach(info);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    /**
     * perform a get to the rest api end point and expect a response containing 20 (default page size) contacts, status
     * 200 and content type application/hal+json;charset=UTF-8
     */
    @Test
    @DisplayName("test get contacts")
    public void whenGetContacts_ThenCorrectStatusAndContentTypeAndSize() throws Exception {
        // @formatter:off
        mockMvc.perform(get(restApiEndPoint).param("size", "34")).andDo(print())
            .andExpect(jsonPath("_embedded.contacts", hasSize(34)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType));
        // @formatter:on
    }

    /**
     * perform a get to the rest api end point and expect a response containing 1 contact (Homer Simpson), status 200
     * and content type application/hal+json;charset=UTF-8
     */
    @Test
    @DisplayName("test get contact")
    public void whenGetContactById_ThenCorrectValuesAndStatusAndContentType() throws Exception {
        // @formatter:off
        mockMvc.perform(get(restApiEndPoint + "/1")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", equalTo("Homer")))
                .andExpect(jsonPath("$.lastName", equalTo("Simpson")))
                .andExpect(jsonPath("$.birthDate", equalTo("1978-06-20")))
                .andExpect(jsonPath("$.married", equalTo(true))).andExpect(jsonPath("$.children", equalTo(3)))
                .andExpect(content().contentType(contentType));
        // @formatter:on
    }

    /**
     * perform a post to the rest api end point supplying a contact json and expect a status 201 and content type
     * application/hal+json;charset=UTF-8 with the created contact in the response body
     */
    @Test
    @DisplayName("test post contact")
    public void whenPostContact_ThenCorrectValuesAndStatus() throws Exception {
        Contact contact = new Contact(new SocialSecurityNumber("123-45-6789"), "Test", "Contact", LocalDate.parse("1980-02-06"),
            Boolean.TRUE, 3);
        String json = json(contact);
        logger.debug("saving contact ", json);
        // @formatter:off
        MvcResult result = mockMvc.perform(post(restApiEndPoint).contentType(contentType).content(json)).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", equalTo("Test")))
                .andExpect(jsonPath("$.lastName", equalTo("Contact")))
                .andReturn();

        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        assertNotNull(id);

        mockMvc.perform(delete(restApiEndPoint + "/" + id).contentType(contentType).content(json)).andDo(print())
               .andExpect(status().isNoContent());
        // @formatter:on
    }

    @Test
    @DisplayName("test put contact")
    public void whenPutContact_ThenCorrectValuesAndStatus() throws Exception {
        Integer id = Integer.valueOf(1);
        Contact contact = new Contact(new SocialSecurityNumber("123-45-6789"), "Homer", "Simpson", LocalDate.parse("1978-06-20"), Boolean.FALSE, 0);
        String json = json(contact);
        logger.debug("putting contact ", json);
        // @formatter:off
        mockMvc.perform(put(restApiEndPoint + "/" + id).contentType(contentType).content(json)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.married", equalTo(false)))
            .andExpect(jsonPath("$.children", equalTo(0)));

        contact.setMarried(true);
        contact.setChildren(3);
        json = json(contact);

        mockMvc.perform(put(restApiEndPoint + "/" + id).contentType(contentType).content(json)).andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.married", equalTo(true)))
        .andExpect(jsonPath("$.children", equalTo(3)));

        // @formatter:on
    }


    /**
     * perform a patch to the rest api end point supplying a contact json and expect a status 200 and content type
     * application/hal+json;charset=UTF-8 with the updated contact in the response body
     */
    @Test
    @DisplayName("test patch contact")
    public void whenPatchContact_ThenCorrectValuesAndStatusAndContentType() throws Exception {
        // @formatter:off
        String homer = "{\"married\" : false, \"children\" : 0}";
        mockMvc.perform(patch(restApiEndPoint + "/1").contentType(contentType).content(homer)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.married", equalTo(false)))
                .andExpect(jsonPath("$.children", equalTo(0)))
                .andExpect(content().contentType(contentType));
        homer = "{\"married\" : true, \"children\" : 3}";

        mockMvc.perform(patch(restApiEndPoint + "/1").contentType(contentType).content(homer)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.married", equalTo(true)))
                .andExpect(jsonPath("$.children", equalTo(3)))
                .andExpect(content().contentType(contentType));
        // @formatter:on
    }

    /** perform a get to the end point with a none existent resource and expect a status 404 - Not Found */
    @Test
    @DisplayName("test get none exising contact")
    public void whenGetNoneExistingContact_ThenStatus404() throws Exception {
        mockMvc.perform(get(restApiEndPoint + "/0")).andDo(print()).andExpect(status().isNotFound());
    }

    /**
     * Utility to convert Contact objects to json string
     *
     * @param contact the contact object
     * @return a json representation of the contact object
     * @throws IOException if anything goes kaput
     */
    private String json(Contact contact) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(contact, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
