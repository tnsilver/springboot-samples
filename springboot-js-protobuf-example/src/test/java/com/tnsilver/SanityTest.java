package com.tnsilver;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SanityTest {

	@Value("${debug}") private Boolean debug;

	@BeforeEach
	void before(TestInfo info) {
		log.debug("begin {} -> {}",info.getTestMethod().get().getName(), info.getDisplayName());
	}

	@Test
	@DisplayName("test context loads")
	void testContextLoads() throws Exception {
		assertFalse(debug);
	}
}
