package com.cc.proxy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.cc.proxy.model.CryptoCoin;

/**
 * The test case InMemoryCacheTest test the functionality of the
 * {@link InMemoryCache} implementation.
 *
 */
@SpringBootTest
class InMemoryCacheTest {

	// @formatter:off
	@Value("${cache.ttl.ms}") private Long cacheTtlMs;
	@Autowired private InMemoryCache<String, CryptoCoin> cache;
	// @formatter:on

	private static final Logger logger = LoggerFactory.getLogger(InMemoryCacheTest.class);
	public static final int MIN_EXPECTED_CACHE_SIZE = 3500;

	@BeforeEach
	void beforeEach(TestInfo info) {
		logger.debug("Entering {}", info.getTestMethod().get().getName());
	}

	@AfterEach
	void afterEach() {
		cache.setTtl(cacheTtlMs);
	}

	@Test
	void testSize() {
		assertNotNull(cache);
		assertTrue(cache.size() > MIN_EXPECTED_CACHE_SIZE);
	}

	@Test
	void testGetTtl() {
		assertEquals(cacheTtlMs, cache.getTtl());
	}

	@Test
	void testSetTtl() {
		cache.setTtl(cache.getTtl() * 2);
		assertEquals(cacheTtlMs * 2, cache.getTtl());
	}

	@Test
	void testPut() {
		CryptoCoin expected = new CryptoCoin();
		expected.setId(123456L);
		expected.setSymbol("TEST");
		expected.setCoinName("TEST");
		expected.setAlgorithm("SHA-256");
		expected.setToUSD(BigDecimal.TEN);
		cache.put(expected.getSymbol(), expected);
		CryptoCoin actual = cache.get(expected.getSymbol());
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	@Test
	void testGet() {
		CryptoCoin expected = cache.get("BTC");
		assertNotNull(expected);
	}

	@Test
	void testRemove() {
		CryptoCoin expected = cache.get("BTC");
		assertNotNull(expected);
		cache.remove(expected.getSymbol());
		expected = cache.get("BTC");
		assertNotNull(expected); // cache auto refresh is default
	}

}
