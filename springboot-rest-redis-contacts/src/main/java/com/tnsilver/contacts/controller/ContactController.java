/*
 * File: ContactController.java
 * Creation Date: Jul 8, 2021
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

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tnsilver.contacts.hateoas.ContactModel;
import com.tnsilver.contacts.hateoas.ContactModelAssembler;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.repository.ContactRepository;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/api")
@ExposesResourceFor(Contact.class)
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);
    // @formatter:off
    @Autowired private ContactRepository contactRepository;
    @Autowired private ContactModelAssembler assembler;
    @Autowired private PagedResourcesAssembler<Contact> pageAssembler;
    //@Autowired private StringRedisTemplate restTemplate;
    // @formatter:on
    private PageImpl<Contact> empty = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10, Sort.unsorted()), 0);

    @PostConstruct
    public void init() {
        pageAssembler.setForceFirstAndLastRels(true);
    }

    // @formatter:off
    @RequestMapping(path = "/contacts/search/byExample", produces = { HAL_JSON_VALUE }, method = POST)
    public ResponseEntity<PagedModel<ContactModel>> getByExample(@RequestBody Contact probe, Pageable pageable) {
        Page<Contact> paged = contactRepository.getByExample(probe, pageable);
        PagedModel<ContactModel> resources = (PagedModel<ContactModel>) pageAssembler.toEmptyModel(empty,Contact.class);
        if (!paged.getContent().isEmpty())
            resources = pageAssembler.toModel(paged, assembler);
        return ResponseEntity.ok(resources);
    }
    // @formatter:on

    // @formatter:off
    @RequestMapping(path = "/contacts/search/byParams", produces = {HAL_JSON_VALUE}, method = GET)
    public ResponseEntity<PagedModel<ContactModel>> getByParams(
            @RequestParam(name = "ssn", required = false) String ssn,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "birthDate", required = false) LocalDate birthDate,
            @RequestParam(name = "married", required = false) Boolean married,
            @RequestParam(name = "children", required = false) Integer children,
            Pageable pageable) {
        Page<Contact> page = contactRepository.getByParams(ssn, firstName, lastName, birthDate, married, children,
                                                           pageable);
        PagedModel<ContactModel> resources = (PagedModel<ContactModel>) pageAssembler.toEmptyModel(empty,
                                                                                                   Contact.class);
        if (!page.getContent().isEmpty())
            resources = pageAssembler.toModel(page, assembler);
            logger.debug("Responding with page {} (sized {}) of {} pages with {} of {} contacts",page.getNumber() + 1,
                                                                                            page.getSize(),
                                                                                            page.getTotalPages(),
                                                                                            page.getContent().size(),
                                                                                            page.getTotalElements());
        return ResponseEntity.ok(resources);
    }
    // @formatter:on

    /*
     *  produces status 201
     */
    @RequestMapping(path = "/contacts", produces = { HAL_JSON_VALUE }, method = { POST })
    public ResponseEntity<ContactModel> post(@RequestBody Contact entity, HttpServletRequest request) {
        entity = contactRepository.save(entity);
        ContactModel resource = assembler.toModel(entity);
        logger.debug("POSTED {}", entity);
        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();
        Link link = resource.getLink("self")
                            .orElse(resource.getLink("contact")
                                            .orElse(Link.of(baseUri + "/api/contacts/" + entity.getId())));
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, link.getHref());
        return new ResponseEntity<>(resource, headers, CREATED);
    }

    /*
     *  PATCH produce status 200 (if success) or 404 if not found
     *  PUT & POST produces status 201, PUT yields 404 if not found
     */
    // @formatter:off
    @RequestMapping(path = "/contacts/{id}", produces = { HAL_JSON_VALUE }, method = { PUT, PATCH })
    public ResponseEntity<ContactModel> update(@PathVariable(name = "id") Long id,
        @RequestBody(required = false) Contact contact, HttpServletRequest request) {
        Assert.notNull(contact, "when calling verb 'PUT' or 'PSOT' a json object describing a contact is expected.");
        ResponseEntity<ContactModel> result = ResponseEntity.notFound().build();
        if (!contactRepository.existsById(id)) // 404 if not found
            return ResponseEntity.notFound().build();
        RequestMethod method = RequestMethod.valueOf(request.getMethod().toUpperCase());
        switch (method) {
            case PUT: {
                contact.setId(id);
                contact = contactRepository.update(contact);
                ContactModel resource = assembler.toModel(contact);
                logger.debug("PUT {}", contact);
                result = new ResponseEntity<>(resource, CREATED);
                break;
            }
            case PATCH: {
                contact.setId(id);
                contact = contactRepository.update(contact);
                ContactModel resource = assembler.toModel(contact);
                logger.debug("PATCHED {}", contact);
                result = ResponseEntity.ok(resource);
                break;
            }
            default:
                break;
        }
        return result;
    }
    // @formatter:on

    /*
     *  GET produce status 200 (if success) or 404 if not found
     */
    @RequestMapping(path = "/contacts/{id}", produces = { HAL_JSON_VALUE }, method = { GET })
    public ResponseEntity<ContactModel> get(@PathVariable(name = "id") Long id) {
        ResponseEntity<ContactModel> result = ResponseEntity.notFound().build();
        if (!contactRepository.existsById(id)) { // 404 if not found
            logger.debug("GET id {} -> {} - {}", id, NOT_FOUND.value(), NOT_FOUND.getReasonPhrase());
            return ResponseEntity.notFound().build();
        }
        Optional<Contact> candidate = contactRepository.findById(id);
        if (candidate.isPresent()) {
            ContactModel resource = assembler.toModel(candidate.get());
            logger.debug("GET {}", candidate.get());
            result = ResponseEntity.ok(resource);
        }
        return result;
    }

    /*
     *  GET produce status 200 (if success) or empty page if not found
     */
    // @formatter:off
    @RequestMapping(path = "/contacts", produces = { HAL_JSON_VALUE }, method = { GET })
    public ResponseEntity<PagedModel<ContactModel>> get(HttpServletRequest request) {
        Iterable<Contact> results = contactRepository.findAll();
        List<Contact> content = IterableConverter.toList(results);
        int size = content.size();
        Page<Contact> page = new PageImpl<Contact>(content, PageRequest.of(0, size, Direction.ASC, "id"), size);
        PagedModel<ContactModel> resources = (PagedModel<ContactModel>) pageAssembler.toEmptyModel(empty,
                                                                                                   Contact.class);
        if (!page.getContent().isEmpty())
            resources = pageAssembler.toModel(page, assembler);
        logger.debug("responding with page {} (sized {}) of {} pages with {} of {} elements",page.getNumber() + 1,
                                                                                             page.getSize(),
                                                                                             page.getTotalPages(),
                                                                                             page.getContent().size(),
                                                                                             page.getTotalElements());
        return ResponseEntity.ok(resources);
    }
    // @formatter:on

    /*
     *  DELETE produce status 204 or 404 if not found
     */
    @RequestMapping(path = "/contacts/{id}", produces = { HAL_JSON_VALUE }, method = { DELETE })
    public ResponseEntity<ContactModel> delete(@PathVariable(name = "id") Long id) {
        if (!contactRepository.existsById(id)) // 404 if not found
            return ResponseEntity.notFound().build();
        ResponseEntity<ContactModel> result = ResponseEntity.notFound().build();
        Optional<Contact> candidate = contactRepository.findById(id);
        if (candidate.isPresent()) {
            contactRepository.deleteById(id);
            logger.debug("DELETED id {}", id);
            result = ResponseEntity.noContent().build();
        }
        return result;
    }

    /*@RequestMapping(path = "/contacts/{id}", method = { GET })
    public void requestDelete(@PathVariable(name = "id") Long id) {
        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, baseUri + "/api/contacts");
        logger.debug("REQUEST DELETE id {}", id);
        boolean delete = restTemplate.delete(baseUri + "/api/contacts/" + id);
        if (delete) {
            logger.debug("DELETED id {}", id);
        } else {
            logger.debug("DID NOT DELETE id {}", id);
        }
    }*/
}
