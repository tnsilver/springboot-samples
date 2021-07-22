/*
 * File: FileNameServiceTest.דבשךש
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
import org.junit.jupiter.api.Assertions.{assertEquals, assertNull}
import org.junit.jupiter.api.{BeforeEach, DisplayName, Test, TestInfo}
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest

import java.io.File
import javax.annotation.Resource
/**
 * The test FileNameServiceTest tests the {@link FileNameService} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest class FileNameServiceTest extends BaseJpaTest {

  @Resource private val fileNameService : FileNameService = null

  @BeforeEach
  @throws[Exception]
  override def beforeEach(info: TestInfo): Unit = {
    super.beforeEach(info)
  }

  @ParameterizedTest
  @DisplayName("test file name service") // @formatter:off
  @CsvSource(Array("a/b/c.txt,c,false",
    "a.txt,a,false",
    "a/b/c,c,false",
    "a/b/c/,'',false",
    "com.tnsilver.contacts.service.FileNameService,FileNameService,true")
  )
  @throws[Exception]
  // @formatter:on
  def whenGivenValidFilePath_ThenGetCorrectFileName(path: String, expected: String, clazz: java.lang.Boolean): Unit = {
    var _path : String = path
    if (clazz)
      _path = java.lang.Class.forName(path).getCanonicalName.replace(".", File.separator) + ".class"
    assertEquals(expected, fileNameService.getName(_path))
  }

  @Test
  @DisplayName("test service with null file name")
  @throws[Exception]
  def whenPassedNullFileName_ThenReturnNull(): Unit = {
    assertNull(fileNameService.getName(null))
  }
}
