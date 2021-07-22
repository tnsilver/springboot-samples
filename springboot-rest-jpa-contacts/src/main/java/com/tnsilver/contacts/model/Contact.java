/*
 * File: Contact.java
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
package com.tnsilver.contacts.model;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
// JPA
@Entity
@Table(name = "contact")
@EntityListeners(AuditingEntityListener.class)
public class Contact implements BaseEntity {

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = null;
    @NotBlank
    @Embedded
    private SocialSecurityNumber ssn;
    @NotNull
    @Size(min = 2, max = 25)
    @Column(name = "first_name")
    private String firstName;
    @Size(min = 2, max = 25)
    @Column(name = "last_name")
    private String lastName;
    @Past
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "married")
    private Boolean married;
    @Min(0)
    @Max(99)
    @Column(name = "children")
    private Integer children;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "created_on", nullable = false, updatable = false, length = 40)
    @CreatedDate
    private LocalDateTime createdOn;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "modified_on", length = 40)
    @LastModifiedDate
    private LocalDateTime modifiedOn;
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
    @LastModifiedBy
    @Column(name = "modified_by", nullable = false, updatable = true)
    private String modifiedBy;
    @Version
    private Integer version;

}
