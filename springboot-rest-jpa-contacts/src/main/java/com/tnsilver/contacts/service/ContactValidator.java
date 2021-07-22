/*
 * File: ContactValidator.java
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
package com.tnsilver.contacts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.tnsilver.contacts.model.BaseEntity;
import com.tnsilver.contacts.model.Contact;

/**
 * Class ContactValidator.
 *
 * @author T.N.Silverman
 */
@Service
public class ContactValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(ContactValidator.class);
    private static final Object[] noArgs = new Object[] {};

    @Override
    public boolean supports(Class<?> clazz) {
        return BaseEntity.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        if (object == null)
            errors.reject("invalid.contact", noArgs, "contact is null");
        else if (object instanceof Contact)
            validateContact((Contact) object, errors);
    }

    private void validateContact(Contact contact, Errors errors) {
        if (contact.getSsn() == null || !contact.getSsn().isValid()) {
            errors.rejectValue("ssn", "invalid.ssn", noArgs, "invalid ssn");
            logger.debug("rejected invalid ssn");
        }
        if (ObjectUtils.isEmpty(contact.getFirstName()) || contact.getFirstName().trim().length() < 2
            || contact.getFirstName().trim().length() > 25) {
            errors.rejectValue("firstName", "invalid.name", noArgs, "invalid name");
            logger.debug("rejected invalid first name");
        }
        if (ObjectUtils.isEmpty(contact.getLastName()) || contact.getLastName().trim().length() < 2
            || contact.getLastName().trim().length() > 25) {
            errors.rejectValue("lastName", "invalid.name", noArgs, "invalid name");
            logger.debug("rejected invalid last name");
        }
        if (contact.getBirthDate() == null) {
            errors.rejectValue("birthDate", "invalid.birthDate", noArgs, "invalid birth date");
            logger.debug("rejected invalid birth date");
        }
        if (contact.getMarried() == null) {
            errors.rejectValue("married", "invalid.married", noArgs, "invalid maritial status");
            logger.debug("rejected invalid maritial status");
        }
        if (contact.getChildren() == null || contact.getChildren() < 0 || contact.getChildren() > 99) {
            errors.rejectValue("children", "invalid.children", new Object[] { 0, 99 }, "invalid number of children");
            logger.debug("rejected invalid number of children");
        }
    }
}
