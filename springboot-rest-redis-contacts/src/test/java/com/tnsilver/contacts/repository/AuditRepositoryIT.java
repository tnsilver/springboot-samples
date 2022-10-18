/*
 * File: AuditRepositoryIT.java
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
package com.tnsilver.contacts.repository;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.security.test.context.support.WithUserDetails;

import com.tnsilver.contacts.base.BaseRedisTest;
import com.tnsilver.contacts.hateoas.ContactModel;
import com.tnsilver.contacts.hateoas.ContactModelAssembler;
import com.tnsilver.contacts.model.Audit;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.SocialSecurityNumber;
import com.tnsilver.contacts.util.RedisUtils;

/**
 * The test AuditRepositoryIT tests the {@link AuditRepositoryImpl} functionality
 *
 * @author T.N.Silverman
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuditRepositoryIT extends BaseRedisTest {

    // @formatter:off
    @Autowired private ContactRepository contactRepository;
    @Autowired private AuditRepository auditRepository;
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private RedisMappingContext mappingContext;
    @Autowired private ContactModelAssembler assembler;
    // @formatter:on

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
    @DisplayName("test auditing new contact")
    @WithUserDetails(value = "user")
    public void whenContactSaved_ThenAudited() {
        // @formatter:off
        var ssn = new SocialSecurityNumber("555-55-5555");
        var contact = new Contact(ssn, "test1", "tester1", LocalDate.now(), false, 0);
        var inceptionDateTime = LocalDateTime.now();
        contact.setCreatedOn(inceptionDateTime);
        contact.setModifiedOn(inceptionDateTime);
        ContactModel expected = assembler.toModel(contact);
        assertAll(() -> assertNull(contact.getId()),
                  () -> assertEquals("system", contact.getCreatedBy()),
                  () -> assertEquals("system", contact.getModifiedBy()));
        var actual = contactRepository.save(contact);
        assertAll(() -> assertNotNull(actual.getId()),
                  () -> assertNotNull(actual.getCreatedBy()),
                  () -> assertEquals(expected.getCreatedBy(), actual.getCreatedBy()),
                  () -> assertNotNull(actual.getCreatedOn()),
                  () -> assertEquals(expected.getCreatedOn(), actual.getCreatedOn()),
                  () -> assertNotNull(actual.getModifiedBy()),
                  () -> assertNotEquals(expected.getModifiedBy(), actual.getModifiedBy()),
                  () -> assertEquals("user", actual.getModifiedBy()),
                  () -> assertNotNull(actual.getModifiedOn()),
                  () -> assertNotEquals(expected.getModifiedOn(), actual.getModifiedOn()));
        // @formatter:on
    }

    @Test
    @DisplayName("test auditing delete contact")
    @WithUserDetails(value = "user")
    public void whenContactDeleted_ThenAudited() {
        // @formatter:off
        var ssn = new SocialSecurityNumber("555-55-5555");
        var contact = new Contact(ssn, "test1", "tester1", LocalDate.now(), false, 0);
        var inceptionDateTime = LocalDateTime.now();
        contact.setCreatedOn(inceptionDateTime);
        contact.setModifiedOn(inceptionDateTime);
        var actual = contactRepository.save(contact);
        var entityId = actual.getId().toString();
        contactRepository.delete(actual);
        var keyspace = RedisUtils.resolveKeySpace(mappingContext, Audit.class);
        var found = new AtomicBoolean(false);
        try (Cursor<String> idCursor = redisTemplate.opsForSet().scan(keyspace, ScanOptions.NONE)) {
            assertNotNull(idCursor);
            assertTrue(idCursor.hasNext());
            logger.trace("scaning audits for 'entityId={}'", entityId);
            idCursor.forEachRemaining(id -> {
                logger.trace("keyspace scanning has '{}'", keyspace + ":" + id);
                try (var auditCursor = redisTemplate.opsForHash()
                    .scan(keyspace + ":" + id, ScanOptions.scanOptions().match("entityId").build())) {
                    auditCursor.forEachRemaining(kv -> {
                        if (kv.getValue().equals(entityId)) {
                            logger.trace("AUDITED! '{}' has '{}={}'", keyspace + ":" + id, kv.getKey(), kv.getValue());
                            found.compareAndSet(false, true);
                        }
                    });
                }
            });
        }
        assertTrue(found.get());
        // @formatter:on
    }

    @Test
    @DisplayName("test save audit")
    @WithUserDetails(value = "user")
    public void whenAuditSaved_ThenHasId() {
        // @formatter:off
        var ssn = new SocialSecurityNumber("555-55-5555");
        var actual = new Contact(ssn, "test1", "tester1", LocalDate.now(), false, 0);
        actual = contactRepository.save(actual);
        var audit = new Audit(actual.getId(), "Contact", "delete", actual.toString());
        audit = auditRepository.save(audit);
        assertNotNull(audit.getId());
        // @formatter:on
    }

    @Test
    @DisplayName("test save audits")
    @WithUserDetails(value = "user")
    public void whenAuditsIterrableSaved_ThenAllHaveHaveIds() {
        // @formatter:off
        var ssn1 = new SocialSecurityNumber("555-55-5555");
        var ssn2 = new SocialSecurityNumber("666-66-6666");
        var c1 = contactRepository.save(new Contact(ssn1, "test1", "tester1", LocalDate.now(), false, 0));
        var c2 = contactRepository.save(new Contact(ssn2, "test2", "tester2", LocalDate.now().minusDays(1), true, 3));
        List<Audit> audits = IterableConverter
                                        .toList(auditRepository.findAll())
                                            .stream()
                                                .filter(a -> a.getEntityId().equals(c1.getId()))
                                                .filter(a -> a.getEntityId().equals(c2.getId()))
                                                .map(a -> {
                                                    a.setId(null);
                                                    return a;
                                                 }).collect(toList());
        var results = auditRepository.saveAll(audits);
        results.forEach(a -> assertNotNull(a.getId()));
        // @formatter:on
    }

    @Test
    @DisplayName("test delete audits")
    @WithUserDetails(value = "user")
    public void whenAuditsIterrableDeleted_ThenAllAreGone() {
        // @formatter:off
        var ssn1 = new SocialSecurityNumber("555-55-5555");
        var ssn2 = new SocialSecurityNumber("666-66-6666");
        var c1 = contactRepository.save(new Contact(ssn1, "test1", "tester1", LocalDate.now(), false, 0));
        var c2 = contactRepository.save(new Contact(ssn2, "test2", "tester2", LocalDate.now().minusDays(1), true, 3));
        List<Audit> audits = IterableConverter
                                        .toList(auditRepository.findAll()).stream()
                                            .filter(a -> a.getEntityId().equals(c1.getId()))
                                            .filter(a -> a.getEntityId().equals(c2.getId()))
                                            .collect(toList());
        auditRepository.deleteAll(audits);
        auditRepository.findAll().forEach(a -> {
            assertNotEquals(a.getEntityId(),c1.getId());
            assertNotEquals(a.getEntityId(),c2.getId());
        });
        // @formatter:on
    }

    @Test
    @DisplayName("test delete audit by ids")
    @WithUserDetails(value = "user")
    public void whenAuditsDeletedByIdIterable_ThenAllAreGone() {
        // @formatter:off
        var ssn1 = new SocialSecurityNumber("555-55-5555");
        var ssn2 = new SocialSecurityNumber("666-66-6666");
        var c1 = contactRepository.save(new Contact(ssn1, "test1", "tester1", LocalDate.now(), false, 0));
        var c2 = contactRepository.save(new Contact(ssn2, "test2", "tester2", LocalDate.now().minusDays(1), true, 3));
        List<Long> auditsIds = IterableConverter
                                            .toList(auditRepository.findAll()).stream()
                                                .filter(a -> a.getEntityId().equals(c1.getId()))
                                                .filter(a -> a.getEntityId().equals(c2.getId()))
                                                .map(Audit::getId)
                                                .collect(toList());
        auditRepository.deleteAllById(auditsIds);
        auditRepository.findAll().forEach(a -> {
            assertNotEquals(a.getEntityId(),c1.getId());
            assertNotEquals(a.getEntityId(),c2.getId());
        });
        // @formatter:on
    }

    @Test
    @DisplayName("test find audit by ids")
    @WithUserDetails(value = "user")
    public void whenFindAuditsByIdIterable_ThenAllAreFound() {
        // @formatter:off
        var ssn1 = new SocialSecurityNumber("555-55-5555");
        var ssn2 = new SocialSecurityNumber("666-66-6666");
        var c1 = contactRepository.save(new Contact(ssn1, "test1", "tester1", LocalDate.now(), false, 0));
        var c2 = contactRepository.save(new Contact(ssn2, "test2", "tester2", LocalDate.now().minusDays(1), true, 3));
        List<Long> auditsIds = IterableConverter
                                            .toList(auditRepository.findAll()).stream()
                                                .filter(a -> a.getEntityId().equals(c1.getId()))
                                                .filter(a -> a.getEntityId().equals(c2.getId()))
                                                .map(Audit::getId)
                                                .collect(toList());
        auditRepository.findAllById(auditsIds).forEach(a -> {
            assertNotEquals(a.getEntityId(),c1.getId());
            assertNotEquals(a.getEntityId(),c2.getId());
        });
        // @formatter:on
    }

    @Test
    @DisplayName("test delete all audits")
    @WithUserDetails(value = "user")
    public void whenDeleteAllAudits_ThenAllAreGone() {
        // @formatter:off
        var ssn1 = new SocialSecurityNumber("555-55-5555");
        var ssn2 = new SocialSecurityNumber("666-66-6666");
        contactRepository.save(new Contact(ssn1, "test1", "tester1", LocalDate.now(), false, 0));
        contactRepository.save(new Contact(ssn2, "test2", "tester2", LocalDate.now().minusDays(1), true, 3));
        auditRepository.deleteAll();
        assertFalse(auditRepository.findAll().iterator().hasNext());
        // @formatter:on
    }

    @Test
    @DisplayName("test update audit")
    @WithUserDetails(value = "user")
    public void whenAuditUpdated_ThenUpdatedFieldsAsExpected() {
        // @formatter:off
        var ssn1 = new SocialSecurityNumber("555-55-5555");
        var ssn2 = new SocialSecurityNumber("666-66-6666");
        var source = new Contact(ssn1, "test1", "tester1", LocalDate.now(), false, 0);
        var target = new Contact(ssn2, "test2", "tester2", LocalDate.now().minusDays(1L), true, 3);
        source = contactRepository.save(source);
        var expected = contactRepository.save(target);
        var audit = new Audit(source.getId(), "Contact", "delete", source.toString());
        audit = auditRepository.save(audit);
        assertNotNull(audit.getId());
        audit.setEntityId(expected.getId());
        audit.setEntityType(expected.getClass().getName());
        audit.setEntity(expected.toString());
        audit = auditRepository.update(audit);
        Optional<Audit> candidate = auditRepository.findById(audit.getId());
        assertTrue(candidate.isPresent());
        var actual = candidate.get();
        assertAll(() -> assertEquals(expected.getId(),actual.getEntityId()),
                  () -> assertEquals(expected.getClass().getName(),actual.getEntityType()),
                  () -> assertEquals(expected.toString(),actual.getEntity()));
        // @formatter:on
    }

    @Test
    @DisplayName("test delete audit")
    @WithUserDetails(value = "user")
    public void whenAuditDeleted_ThenGone() {
        // @formatter:off
        var ssn = new SocialSecurityNumber("555-55-5555");
        var actual = new Contact(ssn, "test1", "tester1", LocalDate.now(), false, 0);
        actual = contactRepository.save(actual);
        var audit = new Audit(actual.getId(), "Contact", "delete", actual.toString());
        audit = auditRepository.save(audit);
        assertNotNull(audit.getId());
        var auditId = audit.getId();
        auditRepository.delete(audit);
        assertTrue(auditRepository.findById(auditId).isEmpty());
        // @formatter:on
    }

    @Test
    @DisplayName("test delete audit by id")
    @WithUserDetails(value = "user")
    public void whenAuditDeletedById_ThenGone() {
        // @formatter:off
        var ssn = new SocialSecurityNumber("555-55-5555");
        var actual = new Contact(ssn, "test1", "tester1", LocalDate.now(), false, 0);
        actual = contactRepository.save(actual);
        var audit = new Audit(actual.getId(), "Contact", "delete", actual.toString());
        audit = auditRepository.save(audit);
        assertNotNull(audit.getId());
        Long auditId = audit.getId();
        auditRepository.deleteById(auditId);
        assertTrue(auditRepository.findById(auditId).isEmpty());
        // @formatter:on
    }

    @Test
    @DisplayName("test exist by id")
    @WithUserDetails(value = "user")
    public void whenCheckAuditExistById_ThenCorrect() {
        // @formatter:off
        var ssn = new SocialSecurityNumber("555-55-5555");
        var c1 = contactRepository.save(new Contact(ssn, "test1", "tester1", LocalDate.now(), false, 0));
        var id = c1.getId();
        contactRepository.delete(c1);
        Long auditId = IterableConverter
                                    .toList(auditRepository.findAll()).stream()
                                        .filter(a -> a.getEntityId().equals(id))
                                        .map(Audit::getId)
                                        .findFirst().orElse(0L);
        assertNotEquals(0L, auditId);
        assertTrue(auditRepository.existsById(auditId));
        // @formatter:on
    }

    @Test
    @DisplayName("test does not exist by id")
    @WithUserDetails(value = "user")
    public void whenCheckAuditExistByFakeId_ThenCorrect() {
        assertFalse(auditRepository.existsById(-1L));
    }

    @Test
    @DisplayName("test count")
    @WithUserDetails(value = "user")
    public void whenCountAudits_ThenCorrect() {
        auditRepository.deleteAll();
        var ssn = new SocialSecurityNumber("555-55-5555");
        var c1 = contactRepository.save(new Contact(ssn, "test1", "tester1", LocalDate.now(), false, 0));
        contactRepository.delete(c1);
        assertEquals(1, auditRepository.count());
    }
}
