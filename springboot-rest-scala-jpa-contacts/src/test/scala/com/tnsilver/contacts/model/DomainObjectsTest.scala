package com.tnsilver.contacts.model

import com.tnsilver.contacts.base.BaseJpaTest
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.{DisplayName, Test}
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.{CsvSource, ValueSource}
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

/*
 * File: DomainObjectsTest.scala
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


/**
 * The test DomainObjectsTest tests the application domain objects {@link Contact}, {@link SocialSecurityNumber},
 * {@link Audit} and {@link Error}
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest class DomainObjectsTest extends BaseJpaTest {

  @ParameterizedTest
  @DisplayName("test contact getters")
  @CsvSource(Array("1,Test,Contact,123-45-6789,1980-08-31,true,2"))
  @throws[Exception]
  def whenCreateContact_ThenAllGettersReturnExpectedValues(id: Long, firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, married: Boolean, children: Integer): Unit = {
    val actual = new Contact(ssn, firstName, lastName, birthDate, married, children)
    actual.setId(id)
    // @formatter:off
    assertAll(
      () => assertNotNull(actual),
      () => assertEquals(id, actual.getId),
      () => assertEquals(firstName, actual.getFirstName),
      () => assertEquals(lastName, actual.getLastName),
      () => assertEquals(ssn, actual.getSsn),
      () => assertEquals(birthDate, actual.getBirthDate),
      () => assertEquals(married, actual.getMarried),
      () => assertEquals(children, actual.getChildren))
    // @formatter:on
  }

  @ParameterizedTest
  @DisplayName("test contact setters")
  @CsvSource(Array("1,Test,Contact,123-45-6789,1980-08-31,true,2"))
  @throws[Exception]
  def whenCreateContact_ThenAllSettersSetExpectedValues(id: Long, firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, married: Boolean, children: Integer): Unit = {
    val actual = new Contact
    actual.setId(id)
    actual.setFirstName(firstName)
    actual.setLastName(lastName)
    actual.setSsn(ssn)
    actual.setBirthDate(birthDate)
    actual.setMarried(married)
    actual.setChildren(children)
    assertAll(
      () => assertNotNull(actual),
      () => assertEquals(id, actual.getId),
      () => assertEquals(firstName, actual.getFirstName),
      () => assertEquals(lastName, actual.getLastName),
      () => assertEquals(ssn, actual.getSsn),
      () => assertEquals(birthDate, actual.getBirthDate),
      () => assertEquals(married, actual.getMarried),
      () => assertEquals(children, actual.getChildren)
    )
  }

  @ParameterizedTest
  @DisplayName("test contact hashcode and equals")
  @CsvSource(Array("1,Test,Contact,123-45-6789,1980-08-31,true,2"))
  @throws[Exception]
  def whenCreateContact_ThenHashCodeFunctionIsOk(id: Long, firstName: String, lastName: String, ssn: SocialSecurityNumber, birthDate: LocalDate, married: Boolean, children: Integer): Unit = {
    val actual = new Contact(ssn, firstName, lastName, birthDate, married, children)
    actual.setId(id)
    val expected = new Contact(ssn, firstName, lastName, birthDate, married, children)
    expected.setId(id)
    expected.setCreatedOn(actual.getCreatedOn)
    expected.setModifiedOn(actual.getModifiedOn)
    assertAll(
      () => assertNotNull(actual),
      () => assertNotNull(expected),
      () => assertEquals(expected, actual),
      () => assertNotNull(expected.hashCode),
      () => assertEquals(expected.toString, actual.toString),
      () => assertEquals(expected.hashCode, actual.hashCode),
      () => assertFalse(expected.equals(null))
    )
  }

  @Test
  @DisplayName("test noargs constructor contact hashcode")
  @throws[Exception]
  def whenCreateDefaultContact_ThenHashCodeFunctionIsStillOk(): Unit = {
    assertEquals(new Contact().hashCode, new Contact().hashCode)
  }

  @Test
  @DisplayName("test contact equals")
  @throws[Exception]
  def whenCreateContact_ThenEqualsOk(): Unit = {
    val actual = new Contact(new SocialSecurityNumber("111-11-1111"), "Test", "Tester", LocalDate.now, false, 0)
    val other = new Contact(new SocialSecurityNumber("222-22-2222"), "Tset", "RetSet", LocalDate.now, true, 1)
    assertTrue(new Contact().equals(new Contact()))
    assertFalse(actual.equals(null))
    assertFalse(new Contact().equals(null))
    assertFalse(new Contact().equals(new Object()))
    assertFalse(actual.equals(other))
    assertTrue(actual.equals(actual))
  }

  @Test
  @DisplayName("test audit equals")
  @throws[Exception]
  def whenCreateAudit_ThenEqualsOk(): Unit = {
    val actual = new Audit(1L, "test", "test", "test")
    val other = new Audit(2L, "tset", "tset", "tset")
    assertTrue(new Audit().equals(new Audit()))
    assertTrue(actual == actual)
    assertFalse(actual.equals(null))
    assertFalse(new Audit().equals(null))
    assertFalse(actual.equals(other))
    assertFalse(new Audit().equals(new Object()))
  }

  @Test
  @DisplayName("test error equals")
  @throws[Exception]
  def whenCreateError_ThenEqualsOk(): Unit = {
    val actual = new Error("test", "test", "test", "test")
    val other = new Error("tset", "tset", "tset", "tset")
    assertTrue(new Error() == new Error)
    assertTrue(actual == actual)
    assertFalse(actual == null)
    assertFalse(new Error().equals(null))
    assertFalse(new Error() == new Object())
    assertFalse(actual == new Object())
    assertFalse(actual == other)
  }

  @Test
  @DisplayName("test ssn equals")
  @throws[Exception]
  def whenCreateSsn_ThenEqualsOk(): Unit = {
    val actual = new SocialSecurityNumber("111-11-1111")
    val other = new SocialSecurityNumber("222-22-2222")
    assertTrue(actual == actual)
    assertFalse(actual == null)
    assertFalse(actual == other)
    assertFalse(new SocialSecurityNumber().equals(null))
    assertFalse(new SocialSecurityNumber() == new Object())
  }

  @Test
  @DisplayName("test ssn as string")
  @throws[Exception]
  def whenGetSsnAsString_ThenExpected(): Unit = {
    val actual = new SocialSecurityNumber("111-11-1111")
    val expected = "111-11-1111"
    assertEquals(expected, actual.getAsString)
  }

  @Test
  @DisplayName("test numbered ssn as string")
  @throws[Exception]
  def whenCreateSsnAsNumbers_ThenGetAsStringExpected(): Unit = {
    val actual = new SocialSecurityNumber(111, 11, 1111)
    val expected = "111-11-1111"
    assertEquals(expected, actual.getAsString)
  }

  @Test
  @DisplayName("test null ssn as empty string")
  @throws[Exception]
  def whenGetNullSsnAsString_ThenExpected(): Unit = {
    val actual = new SocialSecurityNumber()
    val expected = "--"
    assertEquals(expected, actual.getAsString)
  }

  @ParameterizedTest(name = "social security number test")
  @DisplayName("test valid SSN creation")
  @ValueSource(strings = Array("123-45-6789", "100-10-1000"))
  @throws[Exception]
  def whenCreateValidSsn_ThenIsValid(ssn: String): Unit = {
    val actual = new SocialSecurityNumber(ssn)
    assertAll(
      () => assertNotNull(actual),
      () => assertNotNull(actual.getSsn),
      () => assertNotNull(actual.getAsString),
      () => assertTrue(actual.isValid)
    )
  }

  @ParameterizedTest
  @DisplayName("test SSN hashcode")
  @ValueSource(strings = Array("123-45-6789", "100-10-1000"))
  @throws[Exception]
  def whenCreateSsn_ThenHashCodeAndToStringAreOk(ssn: String): Unit = {
    val actual = new SocialSecurityNumber(ssn)
    val expected = new SocialSecurityNumber(ssn)
    assertAll(
      () => assertNotNull(actual),
      () => assertNotNull(expected),
      () => assertEquals(expected, actual),
      () => assertNotNull(expected.hashCode),
      () => assertEquals(expected.toString, actual.toString),
      () => assertEquals(expected.hashCode, actual.hashCode),
      () => assertFalse(expected == null)
    )
  }

  @ParameterizedTest
  @DisplayName("test invalid ssn")
  @ValueSource(strings = Array("123-", "001-10-1000"))
  @throws[Exception]
  def whenCreateInvalidSsn_ThenIsNotValid(ssn: String): Unit = {
    val actual = new SocialSecurityNumber(ssn)
    assertAll(
      () => assertNotNull(actual),
      () => assertFalse(actual.isValid))
  }

  @ParameterizedTest
  @DisplayName("invalid social security number setter test")
  @ValueSource(strings = Array("123-", "000-10-1000", "", "1"))
  @throws[Exception]
  def whenCreateNoargsConstructorSsnAndUseSetter_ThenIsNotValid(ssn: String): Unit = {
    val actual = new SocialSecurityNumber
    actual.setSsn(ssn)
    assertAll(
      () => assertNotNull(actual),
      () => assertFalse(actual.isValid)
    )
  }

  @Test
  @DisplayName("test empty ssn hashcode")
  @throws[Exception]
  def whenCreateDefaultSsn_ThenHashCodeFunctionIsStillOk(): Unit = {
    assertAll(
      () => assertEquals(new SocialSecurityNumber(), new SocialSecurityNumber()),
      () => assertEquals(new SocialSecurityNumber().hashCode, new SocialSecurityNumber().hashCode)
    )
  }

  @ParameterizedTest
  @DisplayName("test audit getters")
  @CsvSource(Array("2,1,com.tnsilver.Blah,test,Blah,tester,1980-08-31 09:31.59"))
  @throws[Exception]
  def whenCreateAudit_ThenGettersGetExpectedValues(id: Long, entityId: Long, entityType: String, actionType: String, entity: String, createdBy: String, timeStamp: String): Unit = {
    val createdOn = LocalDateTime.parse(timeStamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm.ss"))
    assertNotNull(createdOn)
    val actual = new Audit(entityId, entityType, actionType, entity)
    actual.setId(id)
    actual.setCreatedOn(createdOn)
    actual.setCreatedBy(createdBy)
    assertAll(
      () => assertNotNull(actual),
      () => assertEquals(id, actual.getId),
      () => assertEquals(entityId, actual.getEntityId),
      () => assertEquals(entityType, actual.getEntityType),
      () => assertEquals(actionType, actual.getActionType),
      () => assertEquals(entity, actual.getEntity),
      () => assertEquals(createdOn, actual.getCreatedOn),
      () => assertEquals(createdBy, actual.getCreatedBy)
    )
  }

  @ParameterizedTest
  @DisplayName("test audit hashcode")
  @CsvSource(Array("2,1,com.tnsilver.Blah,test,Blah,tester"))
  @throws[Exception]
  def whenCreateAudit_ThenHashcodeWorksOk(id: Long, entityId: Long, entityType: String, actionType: String, entity: String, inceptor: String): Unit = {
    val actual = new Audit(entityId, entityType, actionType, entity)
    val expected = new Audit(entityId, entityType, actionType, entity)
    expected.setCreatedBy(actual.getCreatedBy)
    expected.setCreatedOn(actual.getCreatedOn)
    assertAll(
      () => assertNotNull(actual),
      () => assertNotNull(expected),
      () => assertEquals(expected.toString, actual.toString),
      () => assertEquals(expected.hashCode, actual.hashCode)
    )
  }

  @Test
  @DisplayName("test create noargs constructor audit")
  @throws[Exception]
  def whenCreateNoArgsConstructorAudit_ThenOk(): Unit = {
    assertAll(
      () => assertEquals(new Audit(), new Audit()),
      () => assertEquals(new Audit().hashCode, new Audit().hashCode)
    )
  }

  @ParameterizedTest
  @DisplayName("test error entity getters")
  @CsvSource(Array("com.tnsilver.Blah,test,blah,bad value blah"))
  @throws[Exception]
  def whenCreateError_ThenGettersGetExpectedValues(entity: String, property: String, invalidValue: String, message: String): Unit = {
    val actual = new Error(entity, property, invalidValue, message)
    assertAll(
      () => assertNotNull(actual),
      () => assertEquals(entity, actual.getEntity()),
      () => assertEquals(property, actual.getProperty()),
      () => assertEquals(invalidValue, actual.getInvalidValue()),
      () => assertEquals(message, actual.getMessage()))
  }

  @ParameterizedTest
  @DisplayName("test error entity hashcode")
  @CsvSource(Array("com.tnsilver.Blah,test,blah,bad value blah"))
  @throws[Exception]
  def whenCreateError_ThenHashcodeWorksOk(entity: String, property: String, invalidValue: String, message: String): Unit = {
    val actual = new Error(entity, property, invalidValue, message)
    val expected = new Error(entity, property, invalidValue, message)
    assertAll(
      () => assertNotNull(actual),
      () => assertNotNull(expected),
      () => assertEquals(expected, actual),
      () => assertNotNull(expected.hashCode),
      () => assertEquals(expected.toString, actual.toString),
      () => assertEquals(expected.hashCode, actual.hashCode),
      () => assertFalse(expected == null))
  }

  @Test
  @DisplayName("test noargs constructor error hashcode")
  @throws[Exception]
  def whenCreateNoArgsError_ThenHashcodeStillWorksOk(): Unit = {
    val actual : Error = new Error()
    val expected : Error = new Error()
    assertAll(
      () => assertEquals(expected, actual),
      () => assertEquals(expected.hashCode(), actual.hashCode())
    )
  }
}
