/*
 * File: LocalDateConverter.scala
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

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._

@Component
class LocalDateConverter extends Converter[ String, LocalDate ] {

  private val logger = LoggerFactory.getLogger( classOf[ LocalDateConverter ] )
  @Value( "#{'${date.patterns.csv}'.split(',')}" )
  var patterns : java.util.List[ String ] = _

  override def convert( source : String ) : LocalDate = {
    val allPatterns : collection.mutable.Buffer[ String ] = patterns.asScala
    var localDate : LocalDate = null
    for ( pattern <- allPatterns ) {
      try {
        localDate = LocalDate.parse( source, DateTimeFormatter.ofPattern( pattern ) )
        logger.debug( "parsed local date string '" + source + "' with pattern '" + pattern + "'" )
        return localDate
      } catch {
        case ex : Exception => localDate = null
      }
    }
    localDate
  }

}
