/*
 * File: ContactRepositoryCustom.java
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
package com.tnsilver.contacts.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tnsilver.contacts.model.Contact;

/**
 * A custom repository interface for {@link Contact} hashes to augment functionality not possible on Redis with
 * standard SDR generated repositories such as {@link ContactRepository}.
 *
 * @author T.N.Silverman
 *
 */
public interface ContactRepositoryCustom /*extends QueryByExampleExecutor<Contact> */{

    Page<Contact> getByExample(Contact probe, Pageable pageable);

    Page<Contact> getByParams(String ssn,
                              String firstName,
                              String lastName,
                              LocalDate birthDate,
                              Boolean married,
                              Integer children, Pageable pageable);

    public <S extends Contact> S update(S contact);

}
