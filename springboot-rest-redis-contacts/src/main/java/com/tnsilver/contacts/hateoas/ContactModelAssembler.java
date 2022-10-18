/*
 * File: ContactModelAssembler.java
 * Creation Date: Jul 9, 2021
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
package com.tnsilver.contacts.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.tnsilver.contacts.controller.ContactController;
import com.tnsilver.contacts.model.Contact;

/**
 * Assembles a ContactModel from a Contact instance with {@code self} and {@code contact} links
 *
 * @author T.N.Silverman
 *
 */
@Component
public class ContactModelAssembler extends RepresentationModelAssemblerSupport<Contact, ContactModel> {

    public ContactModelAssembler() {
        super(ContactController.class, ContactModel.class);
    }

    @Override
    public ContactModel toModel(Contact contact) {
        return createResource(contact);
    }

    private ContactModel createResource(Contact contact) {
        ContactModel model = instantiateModel(contact);
        model.id = contact.getId();
        model.ssn = contact.getSsn();
        model.firstName = contact.getFirstName();
        model.lastName = contact.getLastName();
        model.birthDate = contact.getBirthDate();
        model.married = contact.getMarried();
        model.children = contact.getChildren();
        model.createdBy = contact.getCreatedBy();
        model.createdOn = contact.getCreatedOn();
        model.modifiedBy = contact.getModifiedBy();
        model.modifiedOn = contact.getModifiedOn();
        model.version = contact.getVersion();
        // model.add(Link.of(template.expand(model.id).toString(), IanaLinkRelations.SELF));
        model.add(linkTo(methodOn(ContactController.class).get(contact.getId())).withSelfRel());
        model.add(linkTo(methodOn(ContactController.class).get(contact.getId())).withRel("contact"));
        // model.add(Link.of(template.expand(model.id).toString(), IanaLinkRelations.ITEM).withRel("contact"));
        return model;
    }

    @Override
    public CollectionModel<ContactModel> toCollectionModel(Iterable<? extends Contact> contacts) {
        CollectionModel<ContactModel> contactModels = super.toCollectionModel(contacts);
        contactModels.add(linkTo(methodOn(ContactController.class).get((HttpServletRequest) null)).withSelfRel());
        return contactModels;
    }
}
