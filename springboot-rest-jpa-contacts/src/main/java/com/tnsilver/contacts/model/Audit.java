/*
 * File: Audit.java
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class AuditEntry represents an entry in the audit table
 *
 * @author T.N.Silverman
 */
// LOMBOK
@Data
@NoArgsConstructor
// JPA
@Entity
@Table(name = "audit")
@EntityListeners(AuditingEntityListener.class)
public class Audit implements BaseEntity {

    @Serial
    private static final long serialVersionUID = -6239573592972819561L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = null;
    @Column(name = "entity_id")
    private Long entityId = null;
    @Size(max = 25)
    @Column(name = "action")
    private String actionType;
    @Size(max = 25)
    @Column(name = "entity_type")
    private String entityType;
    @Column(name = "entity")
    private String entity;
    @CreatedDate
    @Column(name = "created_on", length = 40)
    private LocalDateTime createdOn;
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    public Audit(Long entityId, String entityType, String actionType, String entity) {
        super();
        this.entityId = entityId;
        this.entityType = entityType;
        this.actionType = actionType;
        this.entity = entity;
        this.createdOn = LocalDateTime.now();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Audit [id=");
        builder.append(id);
        builder.append(", entityId=");
        builder.append(entityId);
        builder.append(", actionType=");
        builder.append(actionType);
        builder.append(", entityType=");
        builder.append(entityType);
        builder.append(", entity=");
        builder.append(entity);
        builder.append(", createdOn=");
        builder.append(createdOn == null ? "" : createdOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm.ss")));
        builder.append(", createdBy=");
        builder.append(createdBy);
        builder.append("]");
        return builder.toString();
    }

}
