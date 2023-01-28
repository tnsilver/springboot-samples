/*
 * File: SecurityConfigTest.java
 * Creation Date: Jul 20, 2021
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
package com.tnsilver.contacts.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import com.tnsilver.contacts.base.BaseJpaTest;


/**
 * Test SecurityConfigTest tests the functionality of the real {@link TestSecurityConfig} class under profile
 * {@code default}
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
// @Transactional
public class SecurityConfigTest extends BaseJpaTest {

    @Test
    @DisplayName("context loads")
    @WithUserDetails
    public void testContextLoads() throws Exception {
    }
}
