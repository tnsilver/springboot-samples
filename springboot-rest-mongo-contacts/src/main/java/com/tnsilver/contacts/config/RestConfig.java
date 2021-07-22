/*
 * File: RestConfig.java
 * Creation Date: Jun 22, 2021
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.tnsilver.contacts.converter.LocalDateConverter;
import com.tnsilver.contacts.converter.LocalDateTimeConverter;
import com.tnsilver.contacts.model.Audit;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.service.ContactValidator;

/**
 * The class RestConfig configures spring-data-rest
 *
 * @author T.N.Silverman
 *
 */
@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Autowired
    private LocalDateConverter localDateConverter;
    @Autowired
    private LocalDateTimeConverter localDateTimeConverter;

    @Autowired
    ContactValidator contactValidator;

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", contactValidator);
        validatingListener.addValidator("beforeSave", contactValidator);
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Contact.class);
        config.exposeIdsFor(Audit.class);
    }

    /**
     * Global (application wide) configuration to avoid the need to define
     * DateTimeFormat annotations on repository query fields of Temporal types.
     * Such {@code DateTimeFormat(iso = DateTimeFormat.ISO.DATE)} annotations
     * restrict the conversion to a single pattern. We want to use custom converters.
     */
    @Override
    public void configureConversionService(ConfigurableConversionService conversionService) {
        conversionService.addConverter(localDateConverter);
        conversionService.addConverter(localDateTimeConverter);
    }

}
