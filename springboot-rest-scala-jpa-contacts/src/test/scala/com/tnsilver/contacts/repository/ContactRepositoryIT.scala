/*
 * File: scala
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
package com.tnsilver.contacts.repository

import com.tnsilver.contacts.base.BaseJpaTest
import com.tnsilver.contacts.model.{Contact, SocialSecurityNumber}
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api._
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.{Page, PageRequest, Pageable, Sort}
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate
import javax.annotation.Resource
/**
 * The test JpaContactRepositoryIT tests the {@link ContactRepository} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
@Transactional class ContactRepositoryIT extends BaseJpaTest {

  private val PAGEABLE : Pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"))

  @Resource private val contactRepository : ContactRepository = null

  @BeforeAll
  @throws[Exception]
  override def beforeAll(): Unit = {
    super.beforeAll()
  }

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
  }

  @ParameterizedTest
  @DisplayName("test get filtered contacts")
  @CsvSource(Array("123-45-6789",
    "123-45-7890",
    "123-45-8901",
    "123-45-9012",
    "123-45-1012"))
  def whenGetFilteredContacts_ThenNotEmpty(ssn: String): Unit = {
    val filter : Contact = new Contact(new SocialSecurityNumber(ssn), null, null, null, null, null)
    val actual : Page[Contact] = contactRepository.getFilteredContacts(filter, PAGEABLE)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
  }

  @ParameterizedTest
  @DisplayName("test get filtered contacts with first name")
  @CsvSource(Array("123-45-6789,H",
    "123-45-7890,Marge",
    "123-45-8901,Lis",
    "123-45-9012,Ba",
    "123-45-1012,Maggi"))
  def whenGetFilteredContactsWithFirstName_ThenNotEmpty(ssn: String, firstName: String): Unit = {
    val filter = new Contact(new SocialSecurityNumber(ssn), firstName, null, null, null, null)
    val actual : Page[Contact] = contactRepository.getFilteredContacts(filter, PAGEABLE)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
  }

  @ParameterizedTest
  @DisplayName("test get filtered contacts with names")
  @CsvSource(Array("123-45-6789,H,Simp",
    "123-45-7890,Marge,Si",
    "123-45-8901,Lis,Simpson",
    "123-45-9012,Ba,Sim",
    "123-45-1012,Maggie,S"))
  def whenGetFilteredContactsWithName_ThenNotEmpty(ssn: String, firstName: String, lastName: String): Unit = {
    val filter = new Contact(new SocialSecurityNumber(ssn), firstName, lastName, null, null, null)
    val actual = contactRepository.getFilteredContacts(filter, PAGEABLE)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
  }

  @ParameterizedTest
  @DisplayName("test get filtered contacts with birth date") // @formatter:off
  @CsvSource(Array("123-45-6789,H,Simp,1978-06-20",
    "123-45-7890,Marge,Si,1979-06-20",
    "123-45-8901,Lis,Simpson,1993-01-13",
    "123-45-9012,Ba,Sim,1995-09-18",
    "123-45-1012,Maggie,S,2003-03-05"))
  def whenGetFilteredContactsWithBirthDate_ThenNotEmpty(ssn: String, firstName: String, lastName: String, birthDate: String): Unit = {
    val filter = new Contact(new SocialSecurityNumber(ssn), firstName, lastName, LocalDate.parse(birthDate), null, null)
    val actual : Page[Contact] = contactRepository.getFilteredContacts(filter, PAGEABLE)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
  }

  @Test def testGetFilteredContactsByMarried(): Unit = {
    val filter : Contact = new Contact(new SocialSecurityNumber("123-45-6789"), "Home", "Simp", LocalDate.parse("1978-06-20"), true, null)
    val pageable : Pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"))
    val actual : Page[Contact] = contactRepository.getFilteredContacts(filter, pageable)
    assertFalse(actual.getContent.isEmpty)
  }

  @Test def testGetFilteredContactsByChildren(): Unit = {
    val filter = new Contact(new SocialSecurityNumber("123-45-6789"), "Home", "Simp", LocalDate.parse("1978-06-20"), true, 3)
    val pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getFilteredContacts(filter, pageable)
    assertFalse(actual.getContent.isEmpty)
  }

  @Test def testGetFilteredContactsByNothing(): Unit = {
    val filter = new Contact
    val pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getFilteredContacts(filter, pageable)
    assertFalse(actual.getContent.isEmpty)
  }

  @ParameterizedTest
  @DisplayName("test get by params") // @Disabled
  // @formatter:off
  @CsvSource(value = Array("123-45-6789,'','','','',''",
    "'','','','','',3",
    "'','','',1978-06-20,'',''",
    "'','','',1978-06-20,true,3",
    "'','',Simp,1978-06-20,true,3",
    "'',Hom,Simp,1978-06-20,true,3",
    "123-45-6789,H,S,1978-06-20,true,3",
    "123-45-6789,H,'',1978-06-20,true,3"))
  // @formatter:on
  def testGetByParams(_ssn: String, firstName: String, lastName: String, _birthDate: String, _married: String, _children: String): Unit = {
    val ssn = if (null == _ssn || _ssn.isBlank) null
    else new SocialSecurityNumber(_ssn)
    val birthDate = if (null == _birthDate || _birthDate.isBlank) null
    else LocalDate.parse(_birthDate)
    val married : java.lang.Boolean = if (null == _married || _married.isBlank) null else java.lang.Boolean.valueOf(_married)
    val children = if (null == _children || _children.isBlank) null
    else Integer.valueOf(_children)
    val filter : Contact = new Contact(ssn, firstName, lastName, birthDate, married, children)
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(if (filter.getSsn == null) null
    else filter.getSsn.getSsn, filter.getFirstName, filter.getLastName, filter.getBirthDate, filter.getMarried, filter.getChildren, pageable)
    assertFalse(actual.getContent.isEmpty)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println("")
  }

  @Test def testGetBySsn(): Unit = {
    val ssn = "123-45-6789"
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(ssn, null, null, null, null, null, pageable)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println()
  }

  @Test def testGetByFirstName(): Unit = {
    val firstName = "Hom"
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(null, firstName, null, null, null, null, pageable)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println()
  }

  @Test def testGetBySSnAndFirstName(): Unit = {
    val ssn = "123-45-6789"
    val firstName = "Hom"
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(ssn, firstName, null, null, null, null, pageable)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println()
  }

  @Test def testGetBySSnAndNames(): Unit = {
    val ssn = "123-45-6789"
    val firstName = "Hom"
    val lastName = "Simp"
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(ssn, firstName, lastName, null, null, null, pageable)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println()
  }

  @Test def testGetBySSnAndNamesAndMarried(): Unit = {
    val ssn = "123-45-6789"
    val firstName = "Hom"
    val lastName = "Simp"
    val married = null
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(ssn, firstName, lastName, null, married, null, pageable)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println()
  }

  @Test def testGetBySSnAndNamesAndMarriedAndChildren(): Unit = {
    val ssn = "123-45-6789"
    val firstName = "Hom"
    val lastName = "Simp"
    val married = true
    val children = Integer.valueOf(3)
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(ssn, firstName, lastName, null, married, children, pageable)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println()
  }

  @Test def testGetBirthDate(): Unit = {
    val birthDate = LocalDate.parse("1978-06-20")
    val pageable = PageRequest.of(0, 1000, Sort.by(Direction.ASC, "id"))
    val actual = contactRepository.getByParams(null, null, null, birthDate, null, null, pageable)
    assertFalse(actual.getContent.isEmpty)
    assertEquals(1, actual.getContent.size)
    System.out.println()
    logger.info("{} results:", actual.getContent.size)
    actual.getContent.forEach((c: Contact) => logger.info("{}", c))
    System.out.println()
  }
}
