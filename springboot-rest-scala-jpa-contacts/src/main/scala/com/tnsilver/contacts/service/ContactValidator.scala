/*
 * File: ContactValidator.scala
 * Creation Date: Feb 4, 2021
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
package com.tnsilver.contacts.service

import com.tnsilver.contacts.model.{BaseEntity, Contact}
import com.tnsilver.contacts.model.BaseEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.{ObjectUtils, StringUtils}
import org.springframework.validation.{Errors, Validator}

/**
 * Class ContactValidator.
 *
 * @author T.N.Silverman
 */
@Service
class ContactValidator extends Validator {

  private val logger = LoggerFactory.getLogger( classOf[ ContactValidator ] )
  private val noArgs = Array[ Object ]()

  override def supports( clazz : Class[ _ ] ) : Boolean = classOf[ BaseEntity ].isAssignableFrom( clazz )

  override def validate( obj : Object, errors : Errors ): Unit = {
    if ( obj == null ) {
      errors.reject( "invalid.contact", noArgs, "contact is null" )
      logger.warn( "rejected null contact" )
    }
    obj match {
      case contact: Contact => validateContact(contact, errors)
      case _ =>
    }
  }

  def validateContact( contact : Contact, errors : Errors ): Unit = {
    logger.info( "validating " + contact + " with Errors type " + errors.getClass.getTypeName )
    if ( contact.getSsn == null || !contact.getSsn.isValid ) {
      errors.rejectValue( "ssn", "invalid.ssn", noArgs, "invalid ssn" )
      logger.warn( "rejected invalid ssn" )
    }
    if ( ObjectUtils.isEmpty( contact.getFirstName ) || contact.getFirstName.trim().length() < 2
      || contact.getFirstName.trim().length() > 25 ) {
      errors.rejectValue( "firstName", "invalid.name", noArgs, "invalid name" )
      logger.warn( "rejected invalid first name" )
    }
    if ( ObjectUtils.isEmpty( contact.getLastName ) || contact.getLastName.trim.length < 2
      || contact.getLastName.trim.length() > 25 ) {
      errors.rejectValue( "lastName", "invalid.name", noArgs, "invalid name" )
      logger.warn( "rejected invalid last name" )
    }
    if ( contact.getBirthDate == null ) {
      errors.rejectValue( "birthDate", "invalid.birthDate", noArgs, "invalid birth date" )
      logger.warn( "rejected invalid birth date" )
    }
    if ( contact.getMarried == null ) {
      errors.rejectValue("married", "invalid.married", noArgs, "invalid maritial status")
      logger.debug("rejected invalid maritial status")
    }
    if ( contact.getChildren < 0 ) {
      errors.rejectValue( "children", "invalid.children", noArgs, "invalid number of children" )
      logger.warn( "rejected invalid number of children" )
    }

  }
}
