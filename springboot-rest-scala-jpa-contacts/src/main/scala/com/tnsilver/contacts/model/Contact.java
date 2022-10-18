/*
 * File: Contact.java
 * Creation Date: Jun 21, 2021
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
import java.util.Objects;

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

//import java.io.Serial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Class Contact.
 *
 * @author T.N.Silverman
 */
@Entity
@Table(name = "contact")
@EntityListeners(AuditingEntityListener.class)
public class Contact implements BaseEntity {

    @Serial
    private static final long serialVersionUID = -5777020224073389111L;

    public Contact() {
        super();
    }

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
        this.version=-1;
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
    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;
    @Version
    private Integer version;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public SocialSecurityNumber getSsn() {
        return ssn;
    }

    public void setSsn(SocialSecurityNumber ssn) {
        this.ssn = ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getMarried() {
        return married;
    }

    public void setMarried(Boolean married) {
        this.married = married;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact contact)) return false;
        return Objects.equals(id, contact.id) && Objects.equals(ssn, contact.ssn) && Objects.equals(firstName, contact.firstName) && Objects.equals(lastName, contact.lastName) && Objects.equals(birthDate, contact.birthDate) && Objects.equals(married, contact.married) && Objects.equals(children, contact.children) && Objects.equals(createdOn, contact.createdOn) && Objects.equals(modifiedOn, contact.modifiedOn) && Objects.equals(createdBy, contact.createdBy) && Objects.equals(modifiedBy, contact.modifiedBy) && Objects.equals(version, contact.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ssn, firstName, lastName, birthDate, married, children, createdOn, modifiedOn, createdBy, modifiedBy, version);
    }

    @Override
    public String toString() {
        return "Contact{" + "id=" + id +
                ", ssn=" + ssn +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", married=" + married +
                ", children=" + children +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", version=" + version +
                '}';
    }
}
