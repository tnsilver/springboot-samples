package com.cc.proxy.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.cc.proxy.model.CryptoCoin;
import com.cc.proxy.model.ProxyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The test case CryptoCompareProxyControllerTest mocks and tests the
 * functionality of the {@link CryptoCompareProxyController} controller.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SuppressWarnings("unchecked")
class CryptoCompareProxyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(CryptoCompareProxyControllerTest.class);

	// @formatter:off
	@Value("${admin.key}") private String headerValue;
	@Value("${admin.key.header}") private String headerName;

	@LocalServerPort private int port;
	@Autowired private WebApplicationContext webApplicationContext;
	@Autowired private ObjectMapper objectMapper;
	// @formatter:on

	private MockMvc mockMvc;

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		logger.debug("entering {}", info.getDisplayName());
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@DisplayName("test get coins")
	void testGetCoins() throws Exception {
		// @formatter:off
		MvcResult result = mockMvc.perform(get("/coins"))
								  .andExpect(status().isOk())
				                  .andExpect(content().contentType(APPLICATION_JSON_VALUE))
				                  .andDo(print())
				                  .andReturn();
		// @formatter:on
		String json = result.getResponse().getContentAsString();
		ProxyResponse<Set<String>> response = objectMapper.readValue(json, ProxyResponse.class);
		assertNotNull(response.getData());
		List<String> coins = (List<String>) response.getData();
		assertNotNull(coins);
		logger.debug("response has {} data entries.", coins.size());
		assertFalse(coins.isEmpty());
	}

	@ParameterizedTest
	@DisplayName("test get coins by algorithm")
	@CsvSource({ "SHA-256", "SHA-512" })
	void testGetCoinByAlgorithm(String algorithm) throws Exception {
		// @formatter:off
		MvcResult result = mockMvc.perform(get("/coins").param("algorithm", algorithm))
								  .andExpect(status().isOk())
				                  .andExpect(content().contentType(APPLICATION_JSON_VALUE))
				                  .andDo(print())
				                  .andReturn();
		// @formatter:on
		String json = result.getResponse().getContentAsString();
		ProxyResponse<Set<String>> response = objectMapper.readValue(json, ProxyResponse.class);
		assertNotNull(response.getData());

		List<String> coins = (List<String>) response.getData();
		assertNotNull(coins);
		logger.debug("response has {} data entries.", coins.size());
		assertFalse(coins.isEmpty());
	}

	@ParameterizedTest
	@DisplayName("test get coins by symbol")
	@CsvSource({ "BTC FRC", "BTC ZZZ IXC", "ACOIN BEAN ZET CLR" })
	void testGetCoinBySymbol(String symbol) throws Exception {
		// @formatter:off
		symbol = symbol.replace(" ", ",");
		MvcResult result = mockMvc.perform(get("/coins").param("symbol", symbol))
								  .andExpect(status().isOk())
				                  .andExpect(content().contentType(APPLICATION_JSON_VALUE))
				                  .andDo(print())
				                  .andReturn();
		// @formatter:on
		String json = result.getResponse().getContentAsString();
		ProxyResponse<Set<String>> response = objectMapper.readValue(json, ProxyResponse.class);
		assertNotNull(response.getData());
		List<String> coins = (List<String>) response.getData();
		assertNotNull(coins);
		logger.debug("response has {} data entries.", coins.size());
		assertFalse(coins.isEmpty());
		assertTrue(coins.size() > 1);
	}

	@ParameterizedTest
	@DisplayName("test get coin symbols")
	@CsvSource({ "BTC", "ZZZ", "42" })
	void testGetCoinSymbol(String symbol) throws Exception {
		// @formatter:off
		MvcResult result = mockMvc.perform(get("/coins/" + symbol))
								  .andExpect(status().isOk())
				                  .andExpect(content().contentType(APPLICATION_JSON_VALUE))
				                  .andDo(print())
				                  .andReturn();
		// @formatter:on
		String json = result.getResponse().getContentAsString();
		ProxyResponse<Map<String, CryptoCoin>> response = objectMapper.readValue(json, ProxyResponse.class);
		assertNotNull(response.getData());
	}

	@ParameterizedTest
	@DisplayName("test set cachttl")
	@CsvSource({ "30000,15000,true", "45000,30000,true", "15000,45000,true", "60000,45000,false" })
	@WithMockUser
	void testSetCachTtl(Integer newValue, Integer oldValue, boolean secured) throws Exception {
		MvcResult result = null;
		// @formatter:off
		if (secured) {
			result = mockMvc.perform(post("/cachettl")
		                  .characterEncoding(Charset.forName("utf8"))
		                  .contentType(MediaType.APPLICATION_JSON)
		                  .header(headerName,headerValue)
		                  .content("{\"ttl\": " + newValue + "}"))
						  .andExpect(status().isCreated())
		                  .andExpect(content().contentType(APPLICATION_JSON_VALUE))
		                  .andDo(print())
		                  .andReturn();
			String json = result.getResponse().getContentAsString();
			ProxyResponse<Map<String, Long>> response = objectMapper.readValue(json, ProxyResponse.class);
			assertNotNull(response.getData());
			LinkedHashMap<String, Long> data = (LinkedHashMap<String, Long>) response.getData();
			assertEquals(data.get("oldValue"), oldValue);
			assertEquals(data.get("newValue"), newValue);
		} else {
			assertThatThrownBy(() -> mockMvc.perform(post("/cachettl")
				                        .characterEncoding(Charset.forName("utf8"))
				                        .contentType(MediaType.APPLICATION_JSON)
				                        .content("{\"ttl\": " + newValue + "}"))
				                        .andExpect(status().isForbidden())).hasCause(new AccessDeniedException("Access is denied"));
		}
		// @formatter:on
	}

}
