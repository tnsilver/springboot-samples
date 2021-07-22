/*
 * File: AuditorProviderService.java
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
package com.tnsilver.contacts.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * The class AuditorProviderService is an AuditorAware<String> implementation. The hashes (model) columns annotated with
 * {@code @CreatedBy} and {@code @LastModifiedBy} are populated with the name of the principal that created or last
 * modified the hash. The required security principal info is pulled from the SecurityContext Authentication instance.
 *
 * @author T.N.Silverman
 */
@Service
public class AuditorProviderService implements AuditorAware<String> {

    private static final Logger logger = LoggerFactory.getLogger(AuditorProviderService.class);
    @Value("${spring.security.system.user.name}")
    String system;
    @Autowired
    private Environment env;

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (null != auth) {
            if (!(auth instanceof AnonymousAuthenticationToken)) {
                String username = ((UserDetails) auth.getPrincipal()).getUsername();
                String authClassName = auth.getClass().getSimpleName();
                logger.trace("NOT AnonymousAuthenticationToken -> user: {}, auth: {}", username, authClassName);
                return Optional.of(username);
            } else {
                List<String> profiles = Stream.of(env.getActiveProfiles()).toList();
                String username = profiles.contains("test") ? "tester"
                    : (profiles.contains("dev") ? "developer" : auth.getName());
                logger.trace("AnonymousAuthenticationToken -> user: {}", username);
                return Optional.of(username);
            }
        }
        logger.trace("No authentication -> user: {}", system);
        return Optional.of(system);
    }
}