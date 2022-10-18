/*
 * File: RedisUtils.java
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
package com.tnsilver.contacts.util;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;
import static org.springframework.data.redis.core.ScanOptions.scanOptions;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.data.keyvalue.core.KeyValueCallback;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.tnsilver.contacts.model.BaseHash;
import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.model.ContactRecord;
/**
 * utilities and helper methods for handling Redis hashes
 *
 * @author T.N.Silverman
 *
 */
public final class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    // @formatter:off
    private static Function<String, Predicate<String>> strMatchFunction = target ->
        example -> null == target || target.matches("^" + Objects.requireNonNullElse(example, "") + ".*$");
    private static Function<LocalDate, Predicate<LocalDate>> localDateMatchFunction = target ->
        example -> target.atStartOfDay().equals(null == example ? target.atStartOfDay() : example.atStartOfDay());
    private static Function<Boolean, Predicate<Boolean>> booleanMatchFunction = target ->
        example -> null == example ? Boolean.TRUE : target.equals(example);
    private static Function<Integer, Predicate<Integer>> intMatchFunction = target ->
        example -> null == example || target.equals(example);
    // @formatter:on

    private RedisUtils() {
        super();
    }

    private static boolean match(String target, String example) {
        return strMatchFunction.apply(target).test(example);
    }

    private static boolean match(LocalDate target, LocalDate example) {
        return localDateMatchFunction.apply(target).test(example);
    }

    private static boolean match(Boolean target, Boolean example) {
        return booleanMatchFunction.apply(target).test(example);
    }

    private static boolean match(Integer target, Integer example) {
        return intMatchFunction.apply(target).test(example);
    }

    // @formatter:off
    /**
     * Returns an array of properties to ignore when copying properties of {@code source} object to another bean.
     * We do not usually want to copy 'all' properties. We're only interested in the ones that are not null on the source object,
     * with the possibility to always ignore some properties (e.g. audit data) regardless whether defined or not on the source object.
     * @param source the source object to copy properties from
     * @param includeNonNulls - when true, the returned array of ignored properties will include only those that are null,
     *                          with the possible addition of the properties forced to be ignored.
     * @param forceIgnore - a variable argument array (ellipsis) of properties that are not to be copied, regardless of their value on the source object
     * @return array of ignored properties not to be copied by {@link BeanUtils} to the target object
     */
    private static String[] getIgnoreProperties(Object source, final boolean includeNonNulls, String... forceIgnore) {
        final BeanWrapper wrapper = new BeanWrapperImpl(source);
        final Predicate<PropertyDescriptor> tester = pd -> includeNonNulls == (null == wrapper.getPropertyValue(pd.getName()));
        String[] ignored = Stream.concat(Arrays.stream(wrapper.getPropertyDescriptors())
                                 .filter(tester)
                                 .map(PropertyDescriptor::getName),
                           Arrays.stream(forceIgnore))
                                 .collect(Collectors.toSet())
                                 .toArray(String[]::new);
        logger.trace("WILL NOT COPY PROPERTIES: {}", Arrays.toString(ignored));
        return ignored;
    }
    // @formatter:on

    // @formatter:off
    /**
     * Returns an array of ignored properties that will not be copied by {@link BeanUtils} and includes only those properties on the source that are null
     * (with the possible addition of the properties forced to be ignored). This will effectively cause BeanUtils to copy only those properties on the
     * source, that are defined.
     * @param source the source object to copy properties from
     * @param forceIgnore - a variable argument array (ellipsis) of properties that are not to be copied, regardless of their value on the source object
     * @return array of ignored properties not to be copied by {@link BeanUtils} to the target object
     */
    private static String[] getIgnoreProperties(Object source, String... forceIgnore) {
        return getIgnoreProperties(source, true, forceIgnore);
    }
    // @formatter:on

    /**
     * short cut method for getting the ignored properties and copying the source properties to the target object
     * @param source the source object to copy properties from
     * @param target the target object to copy properties to
     * @param forceIgnore a variable argument array (ellipsis) of properties that are not to be copied, regardless of their value on the source object
     */
    public static void copyProperties(Object source, Object target, String... forceIgnore) {
        String[] ignoredProperties = getIgnoreProperties(source, forceIgnore);
        BeanUtils.copyProperties(source, target, ignoredProperties);
    }

    // @formatter:off
    /**
     *
     * @param target the target contact (cannot be null)
     * @param example the example contact (cannot be null)
     * @return true if target matches example, taking into account null values, which are considered matching
     * @throws IllegalArgumentException if either target or example are null
     */
    public static boolean isMatchingContact(Contact target, ContactRecord example) {
        Assert.notNull(example, "example contact cannot be null!");
        Assert.notNull(target, "target contact cannot be null!");
        final String targetSsn = (null == target.getSsn())  ? "" : target.getSsn().getSsn();
        return match(targetSsn, example.ssn()) &&
               match(target.getFirstName(), example.firstName()) &&
               match(target.getLastName(), example.lastName()) &&
               match(target.getBirthDate(), example.birthDate()) &&
               match(target.getMarried(), example.married()) &&
               match(target.getChildren(), example.children());
    }
    // @formatter:on

    /**
     * checks if a {@code candidate} object can be cast to a {@code requiredType} class
     */
    public static boolean isTypeMatch(Class<?> requiredType, @Nullable Object candidate) {
        return candidate == null || ClassUtils.isAssignable(requiredType, candidate.getClass());
    }

    /**
     * resolves the keyspace of a base hash model type (e.g. 'Contact' resolves to 'contact'). This is
     * basically the prefix to distinguish between different types of hashes in redis, and it is the value
     * of the RedisHash annotation, when present on the model types.
     */
    public static String resolveKeySpace(RedisMappingContext mappingContext, Class<? extends BaseHash> type) {
        String resolved = mappingContext.getRequiredPersistentEntity(type).getKeySpace();
        logger.trace("class {} resolved to '{}'", type.getName(), resolved);
        return resolved;
    }
    // @formatter:off
    @SuppressWarnings("unchecked")
    public static <T extends BaseHash,TE extends RedisTemplate<String, ?>> T fromHash(TE redisTemplate,
                                                  HashMapper<Object, byte[], byte[]> hashMapper,
                                                  String key) {
        Assert.notNull(redisTemplate, "redisTemplate cannot be null");
        Assert.notNull(key, "hash key cannot be null");
        Map<byte[], byte[]> loadedHash = redisTemplate.opsForHash().entries(key).entrySet()
                                                                   .stream()
                                                                       .map(e -> entry(e.getKey().toString().getBytes(), e.getValue().toString().getBytes()))
                                                                       .collect(toMap(Entry::getKey, Entry::getValue));
        return (T) hashMapper.fromHash(loadedHash);
    }
    // @formatter:on

    // @formatter:off
    public static <T extends BaseHash, TE extends RedisTemplate<String, ?>> T fromHash(TE redisTemplate,
                                                                                       HashMapper<Object, byte[], byte[]> hashMapper,
                                                                                       String keyspace,
                                                                                       String id) {
        Assert.notNull(redisTemplate, "redisTemplate cannot be null");
        Assert.notNull(keyspace, "hash keyspace cannot be null");
        Assert.notNull(id, "hash id cannot be null");
        return fromHash(redisTemplate, hashMapper, (keyspace + ":" + id));
    }
    // @formatter:on

    /**
     * counts hashes in a keyspace.
     * @param redisTemplate the RedisTemplate
     * @param keyspace - the hash keyspace - must not be a hash key (i.e. contains ':')
     * @return the count of the hashes in the {@code keyspace}
     * @throws IllegalArgumentException if any of the arguments are null
     */
    public static <T extends RedisTemplate<String, ?>> Long countInKeyspace(T redisTemplate, String keyspace) {
        Assert.notNull(redisTemplate, "redisTemplate cannot be null");
        Assert.notNull(keyspace, "keyspace cannot be null");
        Assert.doesNotContain(keyspace, ":", "keyspace cannot be a hash key and cannot contain ':' characters");
        try (RedisConnection conn = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()) {
            return conn.sCard(keyspace.getBytes());
        }
    }

    public static <S> S executeRequired(KeyValueTemplate keyValueTemplate, KeyValueCallback<S> action) {
        S result = keyValueTemplate.execute(action);
        if (null != result)
            return result;
        throw new IllegalStateException(String.format("KeyValueCallback %s returned null value!", action));
    }

    // @formatter:off
    public static Long countMatchingContacts(String ssn, String firstName, String lastName,
                                             LocalDate birthDate, Boolean married, Integer children,
                                             Pageable pageable,
                                             RedisMappingContext mappingContext,
                                             KeyValueTemplate keyValueTemplate) {
        ContactRecord example = new ContactRecord(ssn, firstName, lastName, birthDate, married, children);
        final Class<Contact> type = Contact.class;
        KeyValueQuery<?> query = new KeyValueQuery<>(pageable.getSort());
        String keyspace = resolveKeySpace(mappingContext, Contact.class);
        KeyValueCallback<Long> action = adapter -> {
            Iterable<Contact> result = adapter.find(query, keyspace, Contact.class);
            return IterableConverter.toList(result).stream().filter(obj -> isTypeMatch(type, obj))
                                                            .map(obj -> type.cast(obj))
                                                            .filter(contact -> isMatchingContact(contact, example))
                                                            .count();
        };
        Long total = executeRequired(keyValueTemplate, action);
        logger.trace("found {} matches", total);
        return total;
    }
    // @formatter:on

    // @formatter:off
    /**
     * find out if a sequence with a key (e.g {@code 'sequence:contact'}) has a sequence field (e.g {@code 'seq'})
     * @param resdisTemplate - the redisTemplate
     * @param keyspace the keyspace for the sequence (e.g {@code 'sequence:contact'})
     * @param hashkey - the hashkey of the sequence keyspace, storing the sequence (e.g {@code 'seq'})
     * @return true if a sequence with a keyspace (e.g {@code 'sequence:contact'}) has a sequence field (e.g {@code 'seq'}), other wise false.
     */
    public static <T extends RedisTemplate<String, ?>> boolean isSequenceExist(T redisTemplate, String keyspace, String hashkey) {
        boolean exist;
        try (RedisConnection conn = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()) {
            exist = conn.hScan(keyspace.getBytes(),scanOptions().count(1).match(hashkey)
                        .build())
                            .stream()
                                .map(Entry::getKey)
                                .map(String::new)
                                .anyMatch(s -> s.equals(hashkey));
        } catch (Exception ex) {
            String type= ex.getClass().getSimpleName();
            String message = ex.getMessage();
            logger.debug("{} use keyspace like 'sequence:contact' and hashkey like 'seq': {}", type, message);
            Throwable[] suppressed = ex.getSuppressed();
            if (suppressed.length > 0)
                throw new RuntimeException(suppressed[0]);
            return false;
        }
        return exist;
    }
    // @formatter:on
}
