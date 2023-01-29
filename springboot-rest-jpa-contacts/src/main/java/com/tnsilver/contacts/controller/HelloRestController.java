/*
 * File: HelloRestController.java
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

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.tnsilver.contacts.exception.PaymentRequiredException;
import com.tnsilver.contacts.exception.TeapotException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Class HelloRestController.
 *
 * @author T.N.Silverman
 */
@RestController
@RequestMapping("/api")
public class HelloRestController {

    private static final String JsonType = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";
    private static final Gson gson = new Gson();
    @Autowired private ApplicationContext ctx;
    private String greeting;
    private String stranger;

    @PostConstruct
    public void init() {
        Locale locale = LocaleContextHolder.getLocale();
        greeting = ctx.getMessage("hello.greeting", new Object[] {}, "Welcome", locale);
        stranger = ctx.getMessage("hello.stranger", new Object[] {}, "stranger", locale);
    }

    /**
     * Spring (via ResponseEntity, RestController, and/or ResponseBody) will use the contents of the string as the raw
     * response value, rather than treating the string as JSON value to be encoded. This is true even when the
     * controller method uses produces = MediaType.APPLICATION_JSON_VALUE
     * <p>
     * One way to handle this would be to embed your string inside an object or list, so that you're not passing a raw
     * string to Spring. However, that changes the format of your output, and really there's no good reason not to be
     * able to return a properly-encoded JSON string if that's what you want to do. If that's what you want, the best
     * way to handle it is via a JSON formatter such as Json or Google Gson
     */
    @GetMapping(value = "/hello", produces = { JsonType })
    public ResponseEntity<String> greetStranger() {
        String firstName = stranger;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            firstName = authentication.getName();
        }
        return new ResponseEntity<String>(gson.toJson(new Message(greeting + " " + firstName)), HttpStatus.OK);
    }

    @GetMapping(value = "/hello/{firstName}", produces = { JsonType })
    public ResponseEntity<String> greetFirstName(@PathVariable(name = "firstName") String firstName) {
        return new ResponseEntity<String>(gson.toJson(new Message(greeting + " " + firstName)), HttpStatus.OK);
    }

    @GetMapping(value = "/hello/{firstName}/{lastName}", produces = { JsonType })
    public ResponseEntity<String> greetFullName(@PathVariable(name = "firstName") String firstName,
        @PathVariable(name = "lastName") String lastName) {
        return new ResponseEntity<String>(gson.toJson(new Message(greeting + " " + firstName + " " + lastName)), HttpStatus.OK);
    }

    /**
     * ResponseEntity will give you some added flexibility in defining arbitrary HTTP response headers.
     * <p>
     * See the 4th constructor at
     * {@link http://docs.spring.io/spring/docs/3.0.x/api/org/springframework/http/ResponseEntity.html}
     * <p>
     * Some commonly-used ones are Status, Content-Type and Cache-Control. If you don't need that, using @ResponseBody
     * will be a tiny bit more concise.
     */
    @GetMapping(value = "/greet", produces = { JsonType })
    @ResponseBody
    public String greet() {
        return gson.toJson(new Message(greeting + " " + stranger));
    }

    @GetMapping(path = { "/paynow" })
    public void paymentRequiredError(HttpServletResponse response) throws Exception {
        throw new PaymentRequiredException("You owe money!",
            new TeapotException("Darn! I'm a teapot!", new UnsupportedOperationException("You betcha it ain't supported!")));
    }

    @GetMapping(path = { "/teapot" })
    public void teapotError(HttpServletResponse response) throws Exception {
        throw new TeapotException("Darn! I'm a teapot!", new UnsupportedOperationException("You betcha it ain't supported!"));
    }

    class Message implements Serializable {

        @Serial
        private static final long serialVersionUID = -5782125996983566548L;
        String message;

        public Message(String message) {
            this.message = message;
        }
    }
}
