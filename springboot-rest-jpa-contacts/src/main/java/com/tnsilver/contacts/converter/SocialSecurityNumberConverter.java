/*
 * File: SocialSecurityNumberConverter.java
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
package com.tnsilver.contacts.converter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.tnsilver.contacts.model.SocialSecurityNumber;

/**
 * Class SocialSecurityNumberConverter.
 *
 * @author T.N.Silverman
 */
@Component("ssnConverter")
public class SocialSecurityNumberConverter implements Converter<String, SocialSecurityNumber> {

    private static final Logger logger = LoggerFactory.getLogger(SocialSecurityNumberConverter.class);

    @PostConstruct
    public void init() {
        logger.trace("initialized!");
    }

    @Override
    public SocialSecurityNumber convert(String text) {
        logger.trace("converting '{}' to {}...\n", text, SocialSecurityNumber.class.getSimpleName());
        if (ObjectUtils.isEmpty(text)) {
            logger.warn("returning empty ssn");
            return new SocialSecurityNumber();
        }
        if (text.matches("^\\d{3}[- ]?\\d{2}[- ]?\\d{4}$")) {
            try {
                int area = Integer.parseInt(text.substring(0, 3));
                int group = Integer.parseInt(text.substring(4, 6));
                int serial = Integer.parseInt(text.substring(7, 11));
                return new SocialSecurityNumber(area, group, serial);
            } catch (Exception ignore) {
            }
        }
        logger.warn("returning malformed ssn");
        return new SocialSecurityNumber(text);
    }
}
