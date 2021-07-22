/*
 * File: ContactRecord.java
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
package com.tnsilver.contacts.model;

import java.time.LocalDate;

/**
 * The record ContactRecord is a simple DTO for {@link Contact} hashes
 *
 * @author T.N.Silverman
 */
public record ContactRecord(String ssn, String firstName, String lastName, LocalDate birthDate, Boolean married,
    Integer children) {

    public static ContactRecord toContactRecord(Contact contact) {
        return new ContactRecord(null == contact.getSsn() ? null : contact.getSsn().getSsn(), contact.getFirstName(),
                                 contact.getLastName(), contact.getBirthDate(), contact.getMarried(),
                                 contact.getChildren());
    }
}
