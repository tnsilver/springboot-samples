/*
 * File: SocialSecurityNumber.java
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
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class SocialSecurityNumber represents a social security number
 *
 * @author T.N.Silverman
 */
// JPA
@Embeddable
public class SocialSecurityNumber implements Serializable {

    public static final String REGEX = "^\\d{3}-?\\d{2}-?\\d{4}$";
    private static final Logger logger = LoggerFactory.getLogger(SocialSecurityNumber.class);
    @Serial
    private static final long serialVersionUID = 6239698134349222445L;
    @Transient
    private Integer area;
    @Transient
    private Integer group;
    @Transient
    private Integer serial;
    @NotBlank
    @Pattern(regexp = "^\\d{3}-?\\d{2}-?\\d{4}$")
    @Column(name = "contact_ssn")
    private String ssn;

    public SocialSecurityNumber() {
        super();
    }

    public SocialSecurityNumber(String ssn) {
        super();
        try {
            area = Integer.parseInt(ssn.substring(0, 3));
            group = Integer.parseInt(ssn.substring(4, 6));
            serial = Integer.parseInt(ssn.substring(7, 11));
            this.ssn = getAsString();
        } catch (Exception ignore) {
            logger.warn("cannot set ssn {}", ssn);
            this.area = null;
            this.group = null;
            this.serial = null;
            this.ssn = null;
        }
    }

    public SocialSecurityNumber(Integer area, Integer group, Integer serial) {
        super();
        this.area = area;
        this.group = group;
        this.serial = serial;
        this.ssn = getAsString();
    }

    @JsonIgnore
    public String getAsString() {
        return (area == null ? "" : area) + "-" + (group == null ? "" : group) + "-" + (serial == null ? "" : serial);
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        try {
            area = Integer.parseInt(ssn.substring(0, 3));
            group = Integer.parseInt(ssn.substring(4, 6));
            serial = Integer.parseInt(ssn.substring(7, 11));
            this.ssn = getAsString();
        } catch (Exception ex) {
            logger.warn("cannot set ssn {} becuase {} -> {}", ssn, ex.getClass().getTypeName(), ex.getMessage());
            this.area = null;
            this.group = null;
            this.serial = null;
            this.ssn = null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ssn == null) ? 0 : ssn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SocialSecurityNumber other = (SocialSecurityNumber) obj;
        if (ssn == null) {
            return other.ssn == null;
        } else return ssn.equals(other.ssn);
    }

    /*
     * SSN format xxx-xx-xxxx, xxxxxxxxx, xxx-xxxxxx; xxxxx-xxxx: ^\\d{3}: Starts with three numeric digits. [- ]?:
     * Followed by an optional "-" \\d{2}: Two numeric digits after the optional "-" [- ]?: May contain an optional
     * second "-" character. \\d{4}: ends with four numeric digits. Examples: 879-89-8989; 869878789 etc.
     */
    @JsonIgnore
    public boolean isValid() {
        return ssn != null && ssn.matches("^\\d{3}[- ]?\\d{2}[- ]?\\d{4}$");
    }

    @Override
    public String toString() {
        return ssn;
    }
}
