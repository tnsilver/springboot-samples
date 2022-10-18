/*
 * File: Audit.java
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

//import java.io.Serial;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Class AuditEntry represents an entry in the audit table
 *
 * @author T.N.Silverman
 */
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

    public Audit() {
        super();
    }

    public Audit(Long entityId, String entityType, String actionType, String entity) {
        super();
        this.entityId = entityId;
        this.entityType = entityType;
        this.actionType = actionType;
        this.entity = entity;
        this.createdOn = LocalDateTime.now();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Audit audit)) return false;
        return Objects.equals(id, audit.id) && Objects.equals(entityId, audit.entityId) && Objects.equals(actionType, audit.actionType) && Objects.equals(entityType, audit.entityType) && Objects.equals(entity, audit.entity) && Objects.equals(createdOn, audit.createdOn) && Objects.equals(createdBy, audit.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityId, actionType, entityType, entity, createdOn, createdBy);
    }

    @Override
    public String toString() {
        return "Audit [id=" +
                id +
                ", entityId=" +
                entityId +
                ", actionType=" +
                actionType +
                ", entityType=" +
                entityType +
                ", entity=" +
                entity +
                ", createdOn=" +
                (createdOn == null ? "" : createdOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm.ss"))) +
                ", createdBy=" +
                createdBy +
                "]";
    }

}
