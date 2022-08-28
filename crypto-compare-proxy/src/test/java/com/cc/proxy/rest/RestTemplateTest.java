package com.cc.proxy.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.cc.proxy.model.CoinList;
import com.cc.proxy.model.CryptoCoin;
import com.cc.proxy.model.Currency;
import com.cc.proxy.model.Rate;

@SpringBootTest
class RestTemplateTest {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateTest.class);

	// @formatter:off
	@Autowired private TestRestTemplate restTemplate;

	@Value("${coinlist_endpoint:unknown}") private String coinsListUrl;
	@Value("${pricemulti_endpoint:unknown}") private String priceMultiUrl;
	// @formatter:on

	@Test
	@DisplayName("test get coin list")
	//@Disabled
	void testGetCoinsList() throws Exception {
		logger.debug("{}", coinsListUrl);
		ResponseEntity<CoinList> responseEntity = restTemplate.exchange(coinsListUrl, HttpMethod.GET, null,new ParameterizedTypeReference<CoinList>(){});
		CoinList coinList = responseEntity.getBody();
		assertNotNull(coinList);
		assertNotNull(coinList.getData());
		logger.info("coinlist contains {} coins", coinList.getData().size());
		if (!coinList.getResponse().toUpperCase().equals("SUCCESS")) {
			fail(String.format("%s -> %s", coinList.getResponse(), coinList.getMessage()));
		}
		coinList.getData().entrySet().forEach(e -> logger.debug("{} - {}", e.getKey(), e.getValue()));
	}

	@ParameterizedTest
	@DisplayName("test get coin")
	@CsvSource({ "BTC", "GEO", "42", "CASH", "ADA", "LELE" })
	//@Disabled
	void testGetCoin(String fsym) throws Exception {
		String url = coinsListUrl + "?fsym=" + fsym;
		logger.debug("{}", url);
		ResponseEntity<CoinList> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null,new ParameterizedTypeReference<CoinList>(){});
		CoinList coinList = responseEntity.getBody();
		assertNotNull(coinList);
		if (null != coinList.getResponse() && !coinList.getResponse().toUpperCase().equals("SUCCESS")) {
			fail(String.format("%s -> %s", coinList.getResponse(), coinList.getMessage()));
		}
		assertEquals(1, coinList.getData().size());
		coinList.getData().entrySet().forEach(e -> logger.debug("{} - {}", e.getKey(), e.getValue()));
	}

	@ParameterizedTest
	@DisplayName("test get coin")
	@CsvSource(delimiter = ':', value = { "BTC:USD", "ETH,BTC:USD,ILS", "42:ILS,USD,GBP,EUR", "CASH:USD" })
	void testGetCurrency(String fsyms, String tsyms) throws Exception {
		String url = priceMultiUrl + "?fsyms=" + fsyms + "&tsyms=" + tsyms;
		ResponseEntity<Currency> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null,new ParameterizedTypeReference<Currency>(){});
		Currency currency = responseEntity.getBody();
		assertNotNull(currency);
		assertNotNull(currency.getDetails());
		logger.debug("{}",currency);
		if (null != currency.getResponse() && !currency.getResponse().toUpperCase().equals("SUCCESS")) {
			fail(String.format("%s -> %s", currency.getResponse(), currency.getMessage()));
		}
		currency.getDetails().entrySet().forEach(e -> {
			logger.debug("{}", url);
			logger.debug("{}", e.getKey());
			Rate rate = e.getValue();
			rate.getDetails().entrySet().forEach(ie -> logger.debug("{} -> {}", ie.getKey(), ie.getValue()));
		});
	}

	@Test
	@DisplayName("test populate coins list")
	void testPopulateCoinsList() throws Exception {
		final String tsym = "USD";
		// retrieve initial coins list - no currency rate
		logger.debug("{}", coinsListUrl);
		ResponseEntity<CoinList> responseEntity = restTemplate.exchange(coinsListUrl, HttpMethod.GET, null,new ParameterizedTypeReference<CoinList>(){});
		CoinList coinList = responseEntity.getBody();
		assertNotNull(coinList);
		if (!coinList.getResponse().toUpperCase().equals("SUCCESS")) {
			fail(String.format("%s -> %s", coinList.getResponse(), coinList.getMessage()));
		}
		// get the coins list
		Map<String, CryptoCoin> data = coinList.getData();
		// get chunks of symbols in CSV in strings with length as close as possible to
		// max length of 300
		List<String> symbols = getFsymsCSVList(data, 300);
		assertFalse(symbols.isEmpty());
		// for each symbols string, get the USD currency and set it in the data map
		symbols.iterator().forEachRemaining(fsyms -> {
			String url = priceMultiUrl + "?fsyms=" + fsyms + "&tsyms=" + tsym;
			logger.debug("{}", url);
			ResponseEntity<Currency> iResponseEntity = restTemplate.exchange(url, HttpMethod.GET, null,new ParameterizedTypeReference<Currency>(){});
			Currency currency = iResponseEntity.getBody();
			if (null != currency.getResponse() && !currency.getResponse().toUpperCase().equals("SUCCESS")) {
				fail(String.format("%s -> %s", currency.getResponse(), currency.getMessage()));
			}
			currency.getDetails().entrySet().forEach(e -> {
				String symbol = e.getKey();
				Rate rate = e.getValue();
				BigDecimal toUSD = rate.getRate(tsym).orElse(BigDecimal.ZERO);
				if (data.containsKey(symbol))
					data.get(symbol).setToUSD(toUSD);
				else {
					String fsym = data.keySet().stream().filter(k -> k.toUpperCase().equals(symbol.toUpperCase())).findFirst().orElse(symbol);
					if (data.containsKey(fsym))
						data.get(fsym).setToUSD(toUSD);
					else
						logger.warn("symbol {} is not in coins list!", fsym);
				}
			});
		});
		data.entrySet().iterator().forEachRemaining(e -> {
			if (null == e.getValue().getToUSD())
				logger.warn("No USD value for {} -> {}", e.getKey(), e.getValue());
		});

	}

	private List<String> getFsymsCSVList(Map<String, CryptoCoin> data, int maxlen) {
		List<String> result = new LinkedList<>();
		List<String> symbols = data.keySet().stream().toList();
		StringBuilder buffer = new StringBuilder();
		Iterator<String> iter = symbols.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (buffer.length() + s.length() + 1 >= maxlen) {
				buffer.delete(buffer.length() - 1, buffer.length());
				result.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}
			buffer.append(s).append(",");
			if (!iter.hasNext()) {
				buffer.delete(buffer.length() - 1, buffer.length());
				result.add(buffer.toString());
			}
		}
		return result;
	}
}
