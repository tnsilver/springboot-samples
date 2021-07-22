/*
 * File: RedisUtilsTest.java
 * Creation Date: Jul 9, 2021
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
package com.tnsilver.contacts.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;

import com.tnsilver.contacts.base.BaseRedisTest;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.ContactRecord;
import com.tnsilver.contacts.model.SocialSecurityNumber;

@SpringBootTest
public class RedisUtilsTest extends BaseRedisTest {

    @Autowired
    private RedisMappingContext mappingContext;
    @Autowired
    private KeyValueTemplate keyValueTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeAll
    public static void beforeAll() throws Exception {
        BaseRedisTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        super.beforeEach(info);
        flushAndPopulate();
    }

    @Test
    @DisplayName("test resolve key space")
    // @Disabled
    void whenResolveKeyspace_ThenResolved() {
        String expected = RedisUtils.resolveKeySpace(mappingContext, Contact.class);
        String actual = "contact";
        assertNotNull(expected);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test count in key space")
    // @Disabled
    void whenCountInKeyspace_ThenExpected() {
        Long expected = 34L;
        Long actual = RedisUtils.countInKeyspace(redisTemplate, "contact");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test template Sscan")
    // @Disabled
    void whenTemplateScan_ThenCursorReturnsIds() {
        String keyspace = RedisUtils.resolveKeySpace(mappingContext, Contact.class);
        AtomicLong counter = new AtomicLong(0);
        try (Cursor<String> idCursor = redisTemplate.opsForSet().scan(keyspace, ScanOptions.NONE)) {
            assertNotNull(idCursor);
            assertTrue(idCursor.hasNext());
            idCursor.forEachRemaining(id -> {
                logger.trace("cursor got id {}", id);
                counter.incrementAndGet();
                assertEquals(counter.get(), Long.valueOf(id));
            });
        }
    }

    @Test
    @DisplayName("test template HScan")
    // @Disabled
    void whenTemplateHScan_ThenCursorReturnsAllHashKeys() {
        String keyspace = RedisUtils.resolveKeySpace(mappingContext, Contact.class);
        String id = "1";
        String key = keyspace.concat(":").concat(id);
        AtomicLong counter = new AtomicLong();
        try (Cursor<Entry<Object, Object>> hashCursor = redisTemplate.opsForHash().scan(key, ScanOptions.NONE)) {
            assertNotNull(hashCursor);
            assertTrue(hashCursor.hasNext());
            hashCursor.forEachRemaining(e -> {
                logger.trace("{} -> {}", e.getKey(), e.getValue());
                counter.incrementAndGet();
            });
        }
        assertEquals(12, counter.get());
    }

    @Test
    @DisplayName("test is matching matches")
    void whenMatchingContacts_ThenTrue() {
        // @formatter:off
        Contact target = new Contact(101L, new SocialSecurityNumber("555-55-5555"), "test1", "tester1", LocalDate.now(), false, 0);
        // @formatter:on
        ContactRecord example = ContactRecord.toContactRecord(target);
        assertTrue(RedisUtils.isMatchingContact(target, example));
    }

    @Test
    @DisplayName("test is matching does not match")
    void whenNonMatchingContacts_ThenFalse() {
        // @formatter:off
        Contact target = new Contact(101L, new SocialSecurityNumber("555-55-5555"), "test1", "tester1", LocalDate.now(), false, 0);
        // @formatter:on
        ContactRecord example = ContactRecord.toContactRecord(target);
        target.setFirstName("test2");
        assertFalse(RedisUtils.isMatchingContact(target, example));
    }

    @ParameterizedTest
    @DisplayName("test count matching")
    // @formatter:off
     @CsvSource(value = { "'','','','','','',34",
                          "'','','Simp','','','',5",
                          "678-21-4301,'','','','','',1",
                          "'','','','','',3,4",
                          "'','','',1978-06-20,'','',1",
                          "'','','',1978-06-20,true,3,1",
                          "'','',Simp,1978-06-20,true,3,1",
                          "'',Hom,Simp,1978-06-20,true,3,1",
                          "678-21-4301,H,S,1978-06-20,true,3,1",
                          "678-21-4301,H,'',1978-06-20,true,3,1" })
    // @formatter:on
    public void whenCountMatches_ThenCountExpected(String ssn, String firstName, String lastName, String _birthDate,
        String _married, String _children, Long expected) {
        LocalDate birthDate = null == _birthDate || _birthDate.isBlank() ? null : LocalDate.parse(_birthDate);
        Boolean married = null == _married || _married.isBlank() ? null : Boolean.valueOf(_married);
        Integer children = null == _children || _children.isBlank() ? null : Integer.valueOf(_children);
        Pageable pageable = PageRequest.of(0, NUM_OF_CONTACTS, Direction.ASC, "id");
        Long actual = RedisUtils.countMatchingContacts(ssn, firstName, lastName, birthDate, married, children, pageable,
                                                       mappingContext, keyValueTemplate);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test copy properties")
    void whenCopyProps_ThenForceIgnorePropsNotCoppied() {
        // @formatter:off
        Contact source = new Contact(1L, new SocialSecurityNumber("555-55-5555"), "test1", "tester1", LocalDate.now(), false, 0);
        Contact target = new Contact(2L, new SocialSecurityNumber("666-66-6666"), "test2", "tester2", LocalDate.now().minusDays(1), true, 1);
        RedisUtils.copyProperties(source, target, "id", "firstName");
        assertAll(() -> assertNotEquals(target.getId(), source.getId()),
                  () -> assertNotEquals(target.getFirstName(), source.getFirstName()));
        assertAll(() -> assertEquals(target.getSsn(), source.getSsn()),
                  () -> assertEquals(target.getLastName(), source.getLastName()),
                  () -> assertEquals(target.getBirthDate(), source.getBirthDate()),
                  () -> assertEquals(target.getMarried(), source.getMarried()),
                  () -> assertEquals(target.getChildren(), source.getChildren()));
        // @formatter:on
    }

    @Test
    @DisplayName("test sequence exists")
    void whenCallIsequenceExist_ThenCorrectAnswer() {
        assertTrue(RedisUtils.isSequenceExist(redisTemplate, "sequence:audit", "seq"));
        assertTrue(RedisUtils.isSequenceExist(redisTemplate, "sequence:contact", "seq"));
        assertFalse(RedisUtils.isSequenceExist(redisTemplate, "sequence", "seq"));
    }
}
