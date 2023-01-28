/*
 * File: TestRestTemplateConfig.java
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
package com.tnsilver.contacts.config;

import java.time.Duration;
import java.util.List;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Class RestTemplateTestConfig configures a {@link RestTemplate} with a
 * {@link TypeConstrainedMappingJackson2HttpMessageConverter} message converter for {@code test} profile
 *
 * @author T.N.Silverman
 */
@Profile({ "test" })
@Configuration
public class TestRestTemplateConfig {

    @Value("${spring.security.user.name}")
    String user;
    @Value("${spring.security.user.password}")
    String password;

    @Bean
    TestRestTemplate testRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        // @formatter:off
        restTemplateBuilder.additionalMessageConverters(List.of(
            new ByteArrayHttpMessageConverter(),
            new StringHttpMessageConverter(),
            new ResourceHttpMessageConverter(),
            new AllEncompassingFormHttpMessageConverter(),
            new MappingJackson2HttpMessageConverter()));
        // @formatter:on
        restTemplateBuilder.setConnectTimeout(Duration.ofMillis(50000));
        /**
         * avoid exceptions such as
         *
         * I/O error on PATCH request for http://localhost:8080/users: Invalid HTTP method: PATCH;
         * nested exception is java.net.ProtocolException: Invalid HTTP method: PATCH
         *
         * This happens because of the HttpURLConnection used by default in Spring Boot RestTemplate
         * which is provided by the standard JDK HTTP library.
         *
         * This can be resolved by adding new HttpRequestFactory to the RestTemplate.
         *
         * It requires Apache HttpClient library on the class path
         */
        HttpClient httpClient = HttpClientBuilder.create().build();
        final var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplateBuilder.additionalCustomizers(template -> template.setRequestFactory(requestFactory));
        return new TestRestTemplate(restTemplateBuilder).withBasicAuth(user, password);
    }

}
