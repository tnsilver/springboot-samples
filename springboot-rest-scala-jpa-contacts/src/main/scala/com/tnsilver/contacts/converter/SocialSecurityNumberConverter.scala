/*
 * File: SocialSecurityNumberConverter.scala
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
package com.tnsilver.contacts.converter

import com.tnsilver.contacts.model.SocialSecurityNumber
import org.springframework.stereotype.Component
import org.springframework.core.convert.converter.Converter
import org.slf4j.LoggerFactory

import javax.annotation.PostConstruct

@Component( "ssnConverter" )
class SocialSecurityNumberConverter extends Converter[ String, SocialSecurityNumber ] {

  private val logger = LoggerFactory.getLogger( classOf[ SocialSecurityNumberConverter ] )

  @PostConstruct
  def init(): Unit = logger.info( "initialized!" )

  override def convert( text : String ) : SocialSecurityNumber = {
    logger.debug( "converting '{}' to SocialSecurityNumber...\n", text )
    if ( text == null || text.isEmpty || text.isBlank ) {
      logger.warn( "returning empty ssn" )
      return new SocialSecurityNumber()
    }
    if ( text.matches( "^\\d{3}[- ]?\\d{2}[- ]?\\d{4}$" ) ) {
      try {
        val area = Integer.parseInt( text.substring( 0, 3 ) )
        val group = Integer.parseInt( text.substring( 4, 6 ) )
        val serial = Integer.parseInt( text.substring( 7, 11 ) )
        return new SocialSecurityNumber( area, group, serial )
      } catch {
        case ignore : Exception => logger.warn( "{}", ignore )
      }
    }
    logger.warn( "returning malformed ssn" )
    new SocialSecurityNumber( text )
  }

}