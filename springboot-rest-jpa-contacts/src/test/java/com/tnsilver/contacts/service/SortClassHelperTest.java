/*
 * File: SortClassHelperTest.java
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
package com.tnsilver.contacts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tnsilver.contacts.base.BaseJpaTest;

/**
 * Test SortClassHelperTest is a test case for testing the {@link SortClassHelper} functionality
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SortClassHelperTest extends BaseJpaTest{

    @Resource
    private SortClassHelper sortClassHelper;

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @Test
    @DisplayName("test sort class with null sort")
    public void whenCreatedSortClassWithNullSort_ThenGetExpectedConstant() {
        String actual = sortClassHelper.sortClass("id", null);
        assertEquals(SortClassHelper.SORT_BOTH_CLASS, actual);
    }

    @DisplayName("test sort class with empty property")
    public void whenCreatedSortClassWithEmptyProperty_ThenGetExpectedConstant() {
        String actual = sortClassHelper.sortClass("", Sort.by(Sort.Direction.ASC, "id"));
        assertEquals(SortClassHelper.SORT_BOTH_CLASS, actual);
    }

    @Test
    @DisplayName("test sort class with ASC sort")
    public void whenCreatedSortClassWithASCSort_ThenGetExpectedConstant() {
        String actual = sortClassHelper.sortClass("id", Sort.by(Sort.Direction.ASC, "id"));
        assertEquals(SortClassHelper.SORT_ASC_CLASS, actual);
    }

    @Test
    @DisplayName("test sort class with DESC sort")
    public void whenCreatedSortClassWithDESCCSort_ThenGetExpectedConstant() {
        String actual = sortClassHelper.sortClass("id", Sort.by(Sort.Direction.DESC, "id"));
        assertEquals(SortClassHelper.SORT_DESC_CLASS, actual);
    }

    @Test
    @DisplayName("test sort class with unsorted sort")
    public void whenCreatedSortClassWithUnsortedSort_ThenGetExpectedConstant() {
        String actual = sortClassHelper.sortClass("id", Sort.unsorted());
        assertEquals(SortClassHelper.SORT_BOTH_CLASS, actual);
    }
}
