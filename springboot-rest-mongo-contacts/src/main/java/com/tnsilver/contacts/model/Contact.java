/*
 * File: Contact.java
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
package com.tnsilver.contacts.model;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class Contact.
 *
 * @author T.N.Silverman
 */
@Data
@NoArgsConstructor
@Document("contacts")
public class Contact implements BaseDocument {

    @Serial
    private static final long serialVersionUID = -5777020224073389111L;

    public Contact(SocialSecurityNumber ssn, String firstName, String lastName, LocalDate birthDate, Boolean married,
        Integer children) {
        super();
        this.id = -1L;
        this.ssn = ssn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.married = married;
        this.children = children;
        this.createdBy = "system";
        this.createdOn = LocalDateTime.now();
        this.modifiedBy = "system";
        this.modifiedOn = LocalDateTime.now();
    }

    @Id
    private Long id = null;
    @NotBlank
    @Indexed(unique = true, name = "uq_contact_ssn")
    @Field(order = 2)
    private SocialSecurityNumber ssn;
    @NotNull
    @Size(min = 2, max = 25)
    @Field(order = 3)
    private String firstName;
    @Size(min = 2, max = 25)
    @Field(order = 4)
    private String lastName;
    @Past
    @Field(order = 5)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDate;
    @Field(order = 6)
    private Boolean married;
    @Field(order = 7)
    @Min(0)
    @Max(99)
    private Integer children;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Field(order = 8)
    @CreatedDate
    private LocalDateTime createdOn;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Field(order = 9)
    @LastModifiedDate
    private LocalDateTime modifiedOn;
    @Field(order = 10)
    @CreatedBy
    private String createdBy;
    @Field(order = 11)
    @LastModifiedBy
    private String modifiedBy;
    @Version
    @Field(order = 12)
    private Integer version;

}
