/*
 * File: FileNameServiceTest.java
 * Creation Date: Jun 19, 2021
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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

import com.tnsilver.contacts.base.BaseMongoTest;

/**
 * The test FileNameServiceTest tests the {@link FileNameService} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest
public class FileNameServiceTest extends BaseMongoTest {

    @Resource
    private FileNameService fileNameService;

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
    }

    @ParameterizedTest
    @DisplayName("test file name service")
    // @formatter:off
    @CsvSource({ "a/b/c.txt,c,false",
                 "a.txt,a,false",
                 "a/b/c,c,false",
                 "a/b/c/,'',false",
                 "com.tnsilver.contacts.service.FileNameService,FileNameService,true" })
    // @formatter:on
    public void whenGivenValidFilePath_ThenGetCorrectFileName(String path, String expected, boolean clazz) throws Exception {
        if (clazz)
            path = Class.forName(path).getCanonicalName().replace(".", File.separator) + ".class";
        assertEquals(expected, fileNameService.getName(path));
    }

    @Test
    @DisplayName("test service with null file name")
    public void whenPassedNullFileName_ThenReturnNull() throws Exception {
        assertNull(fileNameService.getName(null));
    }
}
