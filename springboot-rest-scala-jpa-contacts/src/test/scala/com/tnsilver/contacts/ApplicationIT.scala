/*
 * File: ApplicationIT.scala
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
package com.tnsilver.contacts

import com.tnsilver.contacts.base.BaseJpaTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api._
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.TestPropertySource

import java.util.concurrent.{CountDownLatch, Executors, Future, TimeUnit}



/**
 * Run this as a regular java program to execute curl commands from the console, without CORS or csrf protection
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource(value = Array("classpath:test.properties")) class ApplicationIT extends BaseJpaTest {

  @LocalServerPort
  private val port : Integer = 0
  @Value("${application.it.test.timeout}") private val timeout : Long = 0
  @Value("${application.it.test.timeunit}") private val unit : TimeUnit = null

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

  /**
   * This test will run the application in test profile, for 10 minutes.
   * This allows the user to browse localhost:8080 and use the application.
   *
   * @throws Exception - if anything goes wrong
   */
  @Test
  @DisplayName("application integration test")
  @throws[Exception]
  def testApplication(): Unit = { // @formatter:off
    logger.info("running application on port {} for {} {}", port, timeout, unit)
    val pool = Executors.newCachedThreadPool
    var future : Future[java.lang.Boolean] = null
    val latch : CountDownLatch = new CountDownLatch(1)
    try {
      future = pool.submit(() => java.lang.Boolean.TRUE)
      latch.await(timeout, unit)
      assertTrue(future.get())
      logger.info("exiting application after {} {}", timeout, unit)
    } catch {
      case interrupt: InterruptedException =>
        Thread.currentThread.interrupt()
        logger.warn("exiting application after because {}", interrupt.initCause(interrupt).getMessage)
    }
    // @formatter:on
  }
}
