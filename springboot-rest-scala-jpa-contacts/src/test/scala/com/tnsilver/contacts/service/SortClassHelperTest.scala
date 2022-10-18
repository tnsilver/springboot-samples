/*
 * File: SortClassHelperTest.scala
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

import org.junit.jupiter.api.Assertions.assertEquals
import javax.annotation.Resource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit.jupiter.SpringExtension
import com.tnsilver.contacts.base.BaseJpaTest



/**
 * Test SortClassHelperTest is a test case for testing the {@link SortClassHelper} functionality
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest class SortClassHelperTest extends BaseJpaTest {

  final val SORT_ASC_CLASS = "sort_asc"
  final val SORT_DESC_CLASS = "sort_desc"
  final val SORT_BOTH_CLASS = "sort_both"

  @Resource private val sortClassHelper : SortClassHelper = null

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
  }

  @Test
  @DisplayName("test sort class with null sort") def whenCreatedSortClassWithNullSort_ThenGetExpectedConstant(): Unit = {
    val actual = sortClassHelper.sortClass("id", null)
    assertEquals(SORT_BOTH_CLASS, actual)
  }

  @DisplayName("test sort class with empty property") def whenCreatedSortClassWithEmptyProperty_ThenGetExpectedConstant(): Unit = {
    val actual = sortClassHelper.sortClass("", Sort.by(Sort.Direction.ASC, "id"))
    assertEquals(SORT_BOTH_CLASS, actual)
  }

  @Test
  @DisplayName("test sort class with ASC sort") def whenCreatedSortClassWithASCSort_ThenGetExpectedConstant(): Unit = {
    val actual = sortClassHelper.sortClass("id", Sort.by(Sort.Direction.ASC, "id"))
    assertEquals(SORT_ASC_CLASS, actual)
  }

  @Test
  @DisplayName("test sort class with DESC sort") def whenCreatedSortClassWithDESCCSort_ThenGetExpectedConstant(): Unit = {
    val actual = sortClassHelper.sortClass("id", Sort.by(Sort.Direction.DESC, "id"))
    assertEquals(SORT_DESC_CLASS, actual)
  }

  @Test
  @DisplayName("test sort class with unsorted sort") def whenCreatedSortClassWithUnsortedSort_ThenGetExpectedConstant(): Unit = {
    val actual = sortClassHelper.sortClass("id", Sort.unsorted)
    assertEquals(SORT_BOTH_CLASS, actual)
  }
}
