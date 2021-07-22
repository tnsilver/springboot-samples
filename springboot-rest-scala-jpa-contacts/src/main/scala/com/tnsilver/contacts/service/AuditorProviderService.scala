/*
 * File: AuditorProviderService.scala
 * Creation Date: Feb 4, 2021
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
package com.tnsilver.contacts.service

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

import java.util.Optional

/**
 * Class AuditorProviderService. The columns annotated with @CreatedBy and @LastModifiedBy are populated with the name
 * of the principal that created or last modified the entity. The information is pulled from SecurityContext‘s
 * Authentication instance. If you want to customize values that are set to the annotated fields, you can implement
 * AuditorAware<T> interface:
 *
 * @author T.N.Silverman
 */
@Service
class AuditorProviderService extends AuditorAware[String] {

    override def  getCurrentAuditor: Optional[String] = {
        val authentication = SecurityContextHolder.getContext.getAuthentication
        Optional.ofNullable(if (authentication == null)  null else authentication.getName).or(() => Optional.of("test"))
    }
}