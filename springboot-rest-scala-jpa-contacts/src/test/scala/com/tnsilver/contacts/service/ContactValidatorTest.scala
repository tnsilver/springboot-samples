/*
 * File: ContactValidatorTest.scala
 * Creation Date: Jun 25, 2021
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

import com.tnsilver.contacts.base.BaseJpaTest
import com.tnsilver.contacts.model.{Contact, SocialSecurityNumber}
import org.junit.jupiter.api.Assertions.{assertAll, assertEquals, assertFalse, assertTrue}
import org.junit.jupiter.api.{BeforeEach, DisplayName, Test, TestInfo}
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.validation.BindException

import java.time.LocalDate
import javax.annotation.Resource


/**
 * The test ContactValidatorTest tests the {@link ContactValidator} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest class ContactValidatorTest extends BaseJpaTest {

  @Resource private val contactValidator: ContactValidator = null

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
  }

  @ParameterizedTest
  @DisplayName("test validator with valid contact")
  @CsvSource(Array("Test,Contact,123-45-6789,1980-08-31,true,2"))
  @throws[Exception]
  def whenCreatedValidContact_ThenValid(firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, married: Boolean, children: Integer): Unit = {
    val contact = new Contact(ssn, firstName, lastName, birthDate, married, children)
    val errors = new BindException(contact, "contact")
    contactValidator.validate(contact, errors)
    assertTrue(errors.getAllErrors.isEmpty)
  }

  @ParameterizedTest
  @DisplayName("test validator with invalid ssn")
  @CsvSource(Array("Test,Contact,123-45-6,1980-08-31,true,2"))
  @throws[Exception]
  def whenCreatedContactWithInvalidSsn_ThenInvalid(firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, married: Boolean, children: Integer): Unit = {
    val contact : Contact = new Contact(ssn, firstName, lastName, birthDate, married, children)
    val errors : BindException = new BindException(contact, "contact")
    contactValidator.validate(contact, errors)
    // @formatter:off
    assertAll(
      () => assertTrue(contactValidator.supports(classOf[Contact])),
      () => assertFalse(errors.getAllErrors.isEmpty),
      () => assertFalse(errors.getFieldErrors.isEmpty),
      () => assertEquals("invalid ssn", errors.getFieldError("ssn").getDefaultMessage)
    )
    // @formatter:on
  }

  @ParameterizedTest
  @DisplayName("test validator with invalid names")
  @CsvSource(Array("T,C,123-45-6789,1980-08-31,true,2"))
  @throws[Exception]
  def whenCreatedContactWithInvalidNames_ThenInvalid(firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, married: Boolean, children: Integer): Unit = {
    val contact = new Contact(ssn, firstName, lastName, birthDate, married, children)
    val errors = new BindException(contact, "contact")
    contactValidator.validate(contact, errors)
    assertAll(
      () => assertTrue(contactValidator.supports(classOf[Contact])),
      () => assertFalse(errors.getAllErrors.isEmpty),
      () => assertFalse(errors.getFieldErrors.isEmpty),
      () => assertEquals("invalid name", errors.getFieldError("firstName").getDefaultMessage),
      () => assertEquals("invalid name", errors.getFieldError("lastName").getDefaultMessage)
    )
  }

  @ParameterizedTest
  @DisplayName("test validator with null birth date")
  @CsvSource(Array("Test,Contact,123-45-6789,true,2"))
  @throws[Exception]
  def whenCreatedContactWithNullBirthdate_ThenInvalid(firstName: String, lastName: String, ssn: SocialSecurityNumber, married: Boolean, children: Integer): Unit = {
    val contact : Contact = new Contact(ssn, firstName, lastName, null, married, children)
    val errors : BindException = new BindException(contact, "contact")
    contactValidator.validate(contact, errors)
    errors.getFieldErrors.forEach(error => logger.info("Error for field {} -> {}", error.getField, error.getDefaultMessage))
    assertAll(
      () => assertTrue(contactValidator.supports(classOf[Contact])),
      () => assertFalse(errors.getAllErrors.isEmpty),
      () => assertFalse(errors.getFieldErrors.isEmpty),
      () => assertEquals("invalid birth date", errors.getFieldError("birthDate").getDefaultMessage)
    )
  }

  @ParameterizedTest
  @DisplayName("test validator with invalid children")
  @CsvSource(Array("T,C,123-45-6789,1980-08-31,true,-1"))
  @throws[Exception]
  def whenCreatedContactWithNegativeChildren_ThenInvalid(firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, married: Boolean, children: Integer): Unit = {
    val contact = new Contact(ssn, firstName, lastName, birthDate, married, children)
    val errors = new BindException(contact, "contact")
    contactValidator.validate(contact, errors)
    assertAll(
      () => assertTrue(contactValidator.supports(classOf[Contact])),
      () => assertFalse(errors.getAllErrors.isEmpty),
      () => assertFalse(errors.getFieldErrors.isEmpty),
      () => assertEquals("invalid number of children", errors.getFieldError("children").getDefaultMessage)
    )
  }

  @ParameterizedTest
  @DisplayName("test validator with null married")
  @CsvSource(Array("T,C,123-45-6789,1980-08-31,1"))
  @throws[Exception]
  def whenCreatedContactWithNullMarried_ThenInvalid(firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, children: Integer): Unit = {
    val contact = new Contact(ssn, firstName, lastName, birthDate, null, children)
    val errors = new BindException(contact, "contact")
    contactValidator.validate(contact, errors)
    assertAll(() => assertTrue(contactValidator.supports(classOf[Contact])),
      () => assertFalse(errors.getAllErrors.isEmpty),
      () => assertFalse(errors.getFieldErrors.isEmpty),
      () => assertEquals("invalid maritial status", errors.getFieldError("married").getDefaultMessage)
    )
  }

  @Test
  @DisplayName("test validator with null contact")
  @throws[Exception]
  def whenNullContact_ThenInvalid(): Unit = {
    val contact = null
    val errors = new BindException(new Contact, "contact")
    contactValidator.validate(contact, errors)
    assertTrue(contactValidator.supports(classOf[Contact]))
    assertFalse(errors.getAllErrors.isEmpty)
    assertEquals(errors.getGlobalError.getDefaultMessage, "contact is null")
  }
}
