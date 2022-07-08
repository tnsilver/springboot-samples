/*
 * File: Contact.java
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
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class Contact is a simple model for a person
 *
 * @author T.N.Silverman
 */
@Data
@NoArgsConstructor
@RedisHash("contact")
public class Contact implements BaseHash {

    @Serial
    private static final long serialVersionUID = -7457326845432316776L;

    @Id
    private Long id;
    @NotBlank
    private SocialSecurityNumber ssn;
    @NotNull
    @Size(min = 2, max = 25)
    @Indexed
    private String firstName;
    @Size(min = 2, max = 25)
    @Indexed
    private String lastName;
    @Past
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Indexed
    private LocalDate birthDate;
    @Indexed
    private Boolean married;
    @Min(0)
    @Max(99)
    @Indexed
    private Integer children;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    private LocalDateTime createdOn;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @LastModifiedDate
    private LocalDateTime modifiedOn;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String modifiedBy;
    @Version
    private Integer version;

    /*
     * no id - no audit constructor
     */
    public Contact(SocialSecurityNumber ssn, String firstName, String lastName, LocalDate birthDate, Boolean married, Integer children) {
        super();
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

    /*
     * no id constructor
     */
    public Contact(SocialSecurityNumber ssn, String firstName, String lastName, LocalDate birthDate, Boolean married, Integer children, LocalDateTime createdOn, LocalDateTime modifiedOn, String createdBy, String modifiedBy, Integer version) {
        this(ssn, firstName, lastName, birthDate, married, children);
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = modifiedOn;
        this.version = version;
    }

    /*
     * all arguments constructor
     */
    public Contact(Long id, SocialSecurityNumber ssn, String firstName, String lastName, LocalDate birthDate, Boolean married, Integer children, LocalDateTime createdOn, LocalDateTime modifiedOn, String createdBy, String modifiedBy, Integer version) {
        this(ssn, firstName, lastName, birthDate, married, children, createdOn, modifiedOn, createdBy, modifiedBy, version);
        this.id = id;
    }


    @PersistenceCreator
    public Contact(Long id, SocialSecurityNumber ssn, String firstName, String lastName, LocalDate birthDate, Boolean married, Integer children) {
        this(ssn, firstName, lastName, birthDate, married, children);
        this.id = id;
    }

}
