/*
 * File: PageableArgumentConverter.java
 * Creation Date: Jul 7, 2021
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
package com.tnsilver.contacts.base;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Scanner;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.TypedArgumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

/**
 * The class PageableArgumentConverter is {@link TypedArgumentConverter} to convert
 * JUnit 5 parameterized test arguments in string page representation of format
 * {@code page=0&size=10&sort=id,ASC}, to a {@link Pageable} instance
 *
 * @author T.N.Silverman
 */
public class PageableArgumentConverter extends TypedArgumentConverter<String, Pageable> {

    private static final Logger logger = LoggerFactory.getLogger(PageableArgumentConverter.class);

    public PageableArgumentConverter() {
        super(String.class, Pageable.class);
    }

    // page=0&size=10&sort=id,ASC
    @Override
    protected Pageable convert(@NotNull String source) throws ArgumentConversionException {
        Assert.notNull(source, "Expecting non-null data string in format \"page=0&size=10&sort=id,ASC\"");
        Assert.hasText(source, "Expecting non-empty data string in format \"page=0&size=10&sort=id,ASC\"");
        int page = 0, size = 10;
        Direction direction = Direction.ASC;
        String[] properties = { "id" };
        try (Scanner scanner = new Scanner(source)) {
            scanner.useDelimiter("&");
            if (scanner.hasNext())
                page = Arrays.stream(scanner.next().split("="))
                             .filter(s -> s.matches("\\d+"))
                             .map(Integer::parseInt)
                             .findAny().orElse(0);
            else
                throw new ArgumentConversionException("expecting page paran part string in the format 'page=0'&size=10&sort=id,ASC");
            if (scanner.hasNext())
                size = Arrays.stream(scanner.next().split("="))
                             .filter(s -> s.matches("\\d+"))
                             .map(Integer::parseInt)
                             .findAny().orElse(10);
            else
                throw new ArgumentConversionException("expecting size param part string in the format page=0'&size=10'&sort=id,ASC");
            if (scanner.hasNext()) {
                String sortpart = Arrays.stream(scanner.next().split("="))
                                        .filter(s -> !s.equals("sort"))
                                        .collect(joining());
                try {
                    direction = Direction.valueOf(sortpart.substring(sortpart.lastIndexOf(",") + 1, sortpart.length()));
                } catch (Exception ex) {
                    logger.warn("cannot resolve direction part because: {}", ex.getMessage());
                    logger.warn("resorting to default direction {}", Direction.ASC);
                    direction = Direction.ASC;
                }
                properties = sortpart.substring(0, sortpart.lastIndexOf(",")).split(",");
            } else
                throw new ArgumentConversionException("expecting sort param part string in the format page=0&size=10'&sort=id,ASC'");
            logger.trace("converted source '{}' to page: {}, size: {}, Sort: {} {}", source, page, size, properties, direction);
        }
        return PageRequest.of(page, size, Sort.by(direction, properties));
    }

}