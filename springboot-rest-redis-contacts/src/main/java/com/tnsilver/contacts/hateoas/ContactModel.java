/*
 * File: ContactModel.java
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
package com.tnsilver.contacts.hateoas;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.tnsilver.contacts.model.SocialSecurityNumber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Relation(collectionRelation = "contacts", itemRelation = "contact")
public class ContactModel extends RepresentationModel<ContactModel> implements Serializable {

    @Serial
    private static final long serialVersionUID = 5950449614748543775L;
    Long id;
    SocialSecurityNumber ssn;
    String firstName;
    String lastName;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate birthDate;
    Boolean married;
    Integer children;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime createdOn;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime modifiedOn;
    String createdBy;
    String modifiedBy;
    Integer version;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(ssn, firstName, lastName, birthDate, married, children, createdBy,
                                               createdOn, modifiedBy, modifiedOn, version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactModel other = (ContactModel) obj;
        return Objects.equals(birthDate, other.birthDate) && Objects.equals(children, other.children)
            && Objects.equals(createdBy, other.createdBy) && Objects.equals(createdOn, other.createdOn)
            && Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
            && Objects.equals(married, other.married) && Objects.equals(modifiedBy, other.modifiedBy)
            && Objects.equals(modifiedOn, other.modifiedOn) && Objects.equals(ssn, other.ssn);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ContactModel [id=");
        builder.append(id);
        builder.append(", ssn=");
        builder.append(ssn);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append(", birthDate=");
        builder.append(birthDate);
        builder.append(", married=");
        builder.append(married);
        builder.append(", children=");
        builder.append(children);
        builder.append(", createdOn=");
        builder.append(createdOn);
        builder.append(", modifiedOn=");
        builder.append(modifiedOn);
        builder.append(", createdBy=");
        builder.append(createdBy);
        builder.append(", modifiedBy=");
        builder.append(modifiedBy);
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }
}
