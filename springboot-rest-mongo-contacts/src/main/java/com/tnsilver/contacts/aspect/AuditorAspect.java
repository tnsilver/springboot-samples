/*
 * File: AuditorAspect.java
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
package com.tnsilver.contacts.aspect;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tnsilver.contacts.model.Audit;
import com.tnsilver.contacts.model.BaseDocument;
import com.tnsilver.contacts.repository.AudtingtRepository;
import com.tnsilver.contacts.service.AuditorProviderService;

/**
 * Class AuditorAspect performs auditing after CRUD activity on {@link BaseDocument} types
 *
 * @author T.N.Silverman
 */
@Profile({ "!jpa" })
@Component
@Aspect
public class AuditorAspect {

    private Logger logger = LoggerFactory.getLogger(AuditorAspect.class);
    @Resource
    private AudtingtRepository auditRepositoy;
    @Resource
    private AuditorProviderService auditorProviderService;

    @Pointcut("execution(@com.tnsilver.contacts.aspect.AuditableMethod * *.*(..))")
    public void auditableMethods() {
    }

    @After("auditableMethods() && args(entity)")
    public void audit(JoinPoint jp, BaseDocument entity) {
        logger.debug("AUDIT: after execution -> auditing delete of {}", entity);
        String createdBy = auditorProviderService.getCurrentAuditor().get();
        String clazz = entity.getClass().getSimpleName();
        Long id = entity.getId();
        Audit entry = new Audit(id, clazz, "delete", entity.toString());
        entry.setCreatedBy(createdBy);
        auditRepositoy.save(entry);
    }
}
