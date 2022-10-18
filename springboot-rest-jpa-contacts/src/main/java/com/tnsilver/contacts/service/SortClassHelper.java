/*
 * File: SortClassHelper.java
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

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service("sortHelper")
public class SortClassHelper {

    public static final String SORT_ASC_CLASS = "sort_asc";
    public static final String SORT_DESC_CLASS = "sort_desc";
    public static final String SORT_BOTH_CLASS = "sort_both";

    /**
     * returns the css sorting class name of the contacts list header page
     *
     * @param propertyName - the name of the property to sort
     * @param sort - the sort parameter obtained from the page object, for example {@code id: ASC} or
     *        {@code firstName: DESC}
     * @return
     */
    public String sortClass(String propertyName, Sort sort) {
        if (propertyName.isBlank() || sort == null) {
            return SORT_BOTH_CLASS;
        }
        String sortStr = sort.toString().toUpperCase();
        if (sortStr.endsWith("ASC") && sortStr.startsWith(propertyName.toUpperCase())) {
            return SORT_ASC_CLASS;
        } else if (sortStr.endsWith("DESC") && sortStr.startsWith(propertyName.toUpperCase())) {
            return SORT_DESC_CLASS;
        } else {
            return SORT_BOTH_CLASS;
        }
    }
}
