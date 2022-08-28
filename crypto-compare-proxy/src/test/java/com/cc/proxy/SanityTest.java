package com.cc.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;

@SpringBootTest
class SanityTest {

	private static final Logger logger = LoggerFactory.getLogger(SanityTest.class);

	// @formatter:off
	@Autowired private Environment env;
	@Autowired private TestRestTemplate restTemplate;

	@Value("${coinlist_endpoint:unknown}") private String coinsListUrl;
	@Value("${pricemulti_endpoint:unknown}") private String priceMultiUrl;
	@Value("${debug}") private Boolean debug;
	// @formatter:on

	@BeforeEach
	void before(TestInfo info) {
		logger.debug("begin {} -> {}",info.getTestMethod().get().getName(), info.getDisplayName());
	}

	@Test
	@DisplayName("test properties init")
	void testPropertiesInitialized() throws Exception {
		assertNotNull(restTemplate);
		assertTrue(coinsListUrl.startsWith("http"));
		assertTrue(priceMultiUrl.startsWith("http"));
		assertFalse(debug);
	}

	@ParameterizedTest
	@DisplayName("test properties loads")
	@CsvSource({ "coinlist_endpoint,https://min-api.cryptocompare.com/data/all/coinlist", "pricemulti_endpoint,https://min-api.cryptocompare.com/data/pricemulti", "spring.security.user.name,user" })
	void testPropertiesSourcesLoaded(String name, String value) {
		assertEquals(value, env.getProperty(name));
	}

}
