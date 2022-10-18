/*
 * File: Error.scala
 * Creation Date: Feb 4, 2020
 *
 * Copyright (c) 2020 T.N.Silverman - all rights reserved
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
package com.tnsilver.contacts.model

import scala.beans.BeanProperty

class Error extends Serializable {

  @BeanProperty
  var entity: String = _
  @BeanProperty
  var property: String = _
  @BeanProperty
  var invalidValue: String = _
  @BeanProperty
  var message: String = _

  def this(entity: String, property: String, invalidValue: String, message: String) = {
    this()
    this.entity = entity
    this.property = property
    this.invalidValue = invalidValue
    this.message = message
  }

  def canEqual(a: Any): Boolean = a.isInstanceOf[Error]

  override def equals(other: Any): Boolean = other match {
    case that: Error =>
      (that canEqual this) &&
        entity == that.entity &&
        property == that.property &&
        invalidValue == that.invalidValue &&
        message == that.message
    case _ => false
  }

  override def toString = s"Error($entity, $property, $invalidValue, $message)"

  override def hashCode(): Int = {
    val state = Seq()
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
