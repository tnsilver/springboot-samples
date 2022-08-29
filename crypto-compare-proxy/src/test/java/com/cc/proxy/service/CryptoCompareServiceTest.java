package com.cc.proxy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cc.proxy.model.CryptoCoin;
import com.cc.proxy.model.Rate;

@SpringBootTest
class CryptoCompareServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(CryptoCompareServiceTest.class);

	// @formatter:off
	@Autowired private CryptoCompareService service;
	// @formatter:on

	@Test
	@DisplayName("test get coinlist data map")
	void testGetCoinlistDataMap() throws Exception {
		Map<String, CryptoCoin> coinsmap = service.getCoinlistData();
		assertNotNull(coinsmap);
		assertFalse(coinsmap.isEmpty());
		logger.debug("found {} coins", coinsmap.size());
	}

	@ParameterizedTest
	@DisplayName("test get single coinlist data map")
	@CsvSource({ "BTC", "GEO", "42" })
	void testGetSingleCoinlistDataMap(String fsym) throws Exception {
		Map<String, CryptoCoin> coinsmap = service.getCoinlistWithPriceMulti(fsym);
		assertNotNull(coinsmap);
		assertEquals(1, coinsmap.size());
		logger.debug("found {} coins", coinsmap.size());
	}

	@ParameterizedTest
	@DisplayName("test get pricemulti data map")
	@CsvSource(delimiter = ':', value = { "BTC:USD", "ETH,BTC:USD,ILS", "42:ILS,USD,GBP,EUR" })
	void testGetPricemultiDataMap(String fsyms, String tsyms) throws Exception {
		Map<String, Rate> currencyRates = service.getPriceMultiData(fsyms, tsyms);
		assertNotNull(currencyRates);
		assertFalse(currencyRates.isEmpty());
		currencyRates.entrySet().forEach(e -> {
			logger.debug("{}", e.getKey());
			Rate rate = e.getValue();
			rate.getDetails().entrySet().forEach(ie -> logger.debug("{} -> {}", ie.getKey(), ie.getValue()));
		});
	}

	@ParameterizedTest
	@DisplayName("test coinlist data map initialized")
	@CsvSource({ "BTC", "GEO", "42", "CASH", "ADA", "AERO", "ALF" })
	void testCoinlistDataMapInitialized(String fsym) {
		Map<String, CryptoCoin> data = service.getCoinlistWithPriceMulti(fsym);
		assertNotNull(data);
		CryptoCoin cryptoCoin = data.get(fsym);
		assertNotNull(cryptoCoin.getToUSD());
		logger.debug("{}", cryptoCoin);
	}
}
