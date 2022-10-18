/*
 * File: AuditorAspect.java
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
package com.tnsilver.contacts.aspect;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.tnsilver.contacts.model.Audit;
import com.tnsilver.contacts.model.BaseHash;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.repository.AuditRepository;
import com.tnsilver.contacts.repository.ContactRepository;
import com.tnsilver.contacts.service.AuditorProviderService;

/**
 * Class AuditorAspect performs auditing after CRUD activity on {@link BaseHash} types
 *
 * @author T.N.Silverman
 */
@Component
@Aspect
public class AuditorAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditorAspect.class);
    @Resource
    private AuditRepository auditRepositoy;
    @Resource
    private ContactRepository contactRepositoy;
    @Resource
    private AuditorProviderService auditorProviderService;
    @Value("${spring.security.system.user.name}")
    String system;

    @Before(argNames = "contact", value = "@annotation(com.tnsilver.contacts.annotation.AuditBeforeDelete) && args(contact)")
    public void auditBeforeDelete(JoinPoint jp, Contact contact) {
        String createdBy = auditorProviderService.getCurrentAuditor().orElse("unknown");
        String clazz = contact.getClass().getSimpleName();
        Audit entry = new Audit(contact.getId(), clazz, "delete", contact.toString());
        entry.setCreatedBy(createdBy);
        auditRepositoy.save(entry);
        String method = Optional.ofNullable(jp.getTarget())
                                .map(o -> o.getClass().getSimpleName())
                                .orElse("")
                                .concat(".")
                                .concat(jp.getSignature().getName())
                                .concat("()");
        logger.trace("AUDITED: {}, entity {}", method, contact);
    }

    @Before(argNames = "id", value = "@annotation(com.tnsilver.contacts.annotation.AuditBeforeDelete) && args(id)")
    public void auditBeforeDeleteById(JoinPoint jp, Long id) {
        Optional<Contact> contact = contactRepositoy.findById(id);
        if (!contact.isPresent()) {
            logger.debug("NOT AUDITING: cannot find hash id {}", id);
            return;
        }
        Contact entity = contact.get();
        String createdBy = auditorProviderService.getCurrentAuditor().orElse("unknown");
        String clazz = entity.getClass().getSimpleName();
        Audit entry = new Audit(id, clazz, "delete", entity.toString());
        entry.setCreatedBy(createdBy);
        auditRepositoy.save(entry);
        String method = Optional.ofNullable(jp.getTarget())
                                .map(o -> o.getClass().getSimpleName())
                                .orElse("")
                                .concat(".")
                                .concat(jp.getSignature().getName())
                                .concat("()");
        logger.trace("AUDITED: {}, entity {}", method, entity);
    }

    @Before(argNames = "contact", value = "@annotation(com.tnsilver.contacts.annotation.AuditBeforeModification) && args(contact)")
    public void auditBeforeModify(JoinPoint jp, Contact contact) {
        Assert.notNull(contact, "contact argument cannot be null!");
        Class<Contact> requiredType = Contact.class;
        Class<?> candidate = contact.getClass();
        String errmsg = "AUDIT FAILURE: entity is a '%s' but required '%s'";
        Assert.isAssignable(requiredType, candidate,
                            () -> String.format(errmsg, candidate.getName(), requiredType.getName()));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        if (null == contact.getCreatedOn())
            contact.setCreatedOn(now);
        String username = auditorProviderService.getCurrentAuditor().orElse(system);
        if (null == contact.getCreatedBy())
            contact.setCreatedBy(username);
        contact.setModifiedOn(now);
        contact.setModifiedBy(username);
        String method = Optional.ofNullable(jp.getTarget())
                                .map(o -> o.getClass().getSimpleName())
                                .orElse("")
                                .concat(".")
                                .concat(jp.getSignature().getName())
                                .concat("()");
        logger.trace("AUDITED: {}, entity {}", method, contact);
    }

    @Before(argNames = "contacts", value = "@annotation(com.tnsilver.contacts.annotation.AuditBeforeModification) && args(contacts)")
    public <S extends Contact> void auditBeforeModifyAll(JoinPoint jp, Iterable<S> contacts) {
        Assert.notNull(contacts, "contacts argument cannot be null!");
        contacts.forEach(contact -> {
            Class<Contact> requiredType = Contact.class;
            Class<?> candidate = contact.getClass();
            String errmsg = "AUDIT FAILURE: entity is a '%s' but required '%s'";
            Assert.isAssignable(requiredType, candidate,
                                () -> String.format(errmsg, candidate.getName(), requiredType.getName()));
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            if (null == contact.getCreatedOn())
                contact.setCreatedOn(now);
            String username = auditorProviderService.getCurrentAuditor().orElse(system);
            if (null == contact.getCreatedBy())
                contact.setCreatedBy(username);
            contact.setModifiedOn(now);
            contact.setModifiedBy(username);
            String method = Optional.ofNullable(jp.getTarget())
                                    .map(o -> o.getClass().getSimpleName())
                                    .orElse("")
                                    .concat(".")
                                    .concat(jp.getSignature().getName())
                                    .concat("()");
            logger.debug("AUDITED: {}, entity {}", method, contact);
        });
    }
    // @formatter:on
}
