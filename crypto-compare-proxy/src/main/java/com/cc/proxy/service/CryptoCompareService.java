package com.cc.proxy.service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cc.proxy.model.CoinList;
import com.cc.proxy.model.CryptoCoin;
import com.cc.proxy.model.Currency;
import com.cc.proxy.model.Rate;

import lombok.extern.slf4j.Slf4j;

/**
 * The class CryptoCompareService is the default implementation of
 * {@link CryptoCompareService}.
 */
@Service
@Slf4j
public class CryptoCompareService {

	//public final static String SUCCESS = "SUCCESS";
	public final static int MAX_FSYMS_LENGTH = 300;
	public final static int MAX_TSYMS_LENGTH = 100;
	public final static int MAX_FSYM_LENGTH = 30;
	public final static String USD_CURRENCY_CODE = "USD";

	// @formatter:off
	@Autowired RestTemplate restTemplate;
	@Value("${coinlist_endpoint:unknown}") private String coinsListUrl;
	@Value("${pricemulti_endpoint:unknown}") private String priceMultiUrl;
	// @formatter:on

	public Map<String, CryptoCoin> getCoinlistWithPriceMulti(String fsym) throws IllegalArgumentException {
		validateCoinlistArgs(fsym);
		String url = coinsListUrl + "?fsym=" + fsym;
		log.info("accessing REST API endpoint {}", url);
		ResponseEntity<CoinList> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null,new ParameterizedTypeReference<CoinList>(){});
		CoinList coinList = responseEntity.getBody();
		return getPriceMultiData(coinList.getData(), MAX_FSYM_LENGTH);
	}

	public Map<String, Rate> getPriceMultiData(String fsyms, String tsyms)
			throws IllegalArgumentException {
		validatePriceMultiArgs(fsyms, tsyms);
		String url = priceMultiUrl + "?fsyms=" + fsyms + "&tsyms=" + tsyms;
		log.info("accessing REST API endpoint {}", url);
		ResponseEntity<Currency> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null,new ParameterizedTypeReference<Currency>(){});
		Currency currency = responseEntity.getBody();
		return currency.getDetails();
	}

	public Map<String, CryptoCoin> getCoinlistData() throws IllegalArgumentException {
		log.info("accessing REST API endpoint {}", coinsListUrl);
		Map<String, CryptoCoin> data = getCoinlistInternal();
		log.debug("returning {} result entries", data.size());
		return data;
	}

	private Map<String, CryptoCoin> getPriceMultiData(Map<String, CryptoCoin> data, int maxlen)
			throws IllegalArgumentException {
		List<String> symbols = getSymbolsCSVInternal(data, maxlen);
		Iterator<String> siter = symbols.iterator();
		while (siter.hasNext()) {
			String fsyms = siter.next();
			String url = priceMultiUrl + "?fsyms=" + fsyms + "&tsyms=" + USD_CURRENCY_CODE;
			try {
				log.info("accessing REST API endpoint {}", url);
				ResponseEntity<Currency> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null,new ParameterizedTypeReference<Currency>(){});
				Currency currency = responseEntity.getBody();
				Iterator<Entry<String, Rate>> citer = currency.getDetails().entrySet().iterator();
				while (citer.hasNext()) {
					Entry<String, Rate> e = citer.next();
					String symbol = e.getKey();
					Rate rate = e.getValue();
					BigDecimal toUSD = rate.getRate(USD_CURRENCY_CODE).orElse(BigDecimal.ZERO);
					if (data.containsKey(symbol)) {
						data.get(symbol).setToUSD(toUSD);
					} else {
						String fsym = data.keySet().stream().filter(k -> k.toUpperCase().equals(symbol.toUpperCase())).findAny().orElse(symbol);
						log.warn("looking up suspected missing crypto coin symbol '{}'...", fsym);
						if (data.containsKey(fsym)) {
							data.get(fsym).setToUSD(toUSD);
						} else {
							log.warn("crypto coin symbol '{}' not retrieved from REST API endpoint {}", fsym, url);
						}
					}
				}
			} catch (HttpStatusCodeException ex) {
				log.error("Received error: {}", ex.getResponseBodyAsString(), ex);
			}
		}
		log.debug("returning {} entries of data", data.size());
		return data;
	}

	private Map<String, CryptoCoin> getCoinlistInternal() {
		ResponseEntity<CoinList> responseEntity = restTemplate.exchange(coinsListUrl, HttpMethod.GET, null,new ParameterizedTypeReference<CoinList>(){});
		CoinList coinList = responseEntity.getBody();
		Map<String, CryptoCoin> data = coinList.getData();
		log.info("retrieved {} crypto coins from {}", data.size(), coinsListUrl);
		return data;
	}

	private void validateCoinlistArgs(String fsym) {
		if (null == fsym || fsym.isBlank() || fsym.length() > MAX_FSYM_LENGTH)
			throw new IllegalArgumentException(String.format(
					"argument 'fsym' is required, and cannot exceed a length of {} characters. See {} for details.",
					MAX_FSYM_LENGTH,
					"https://min-api.cryptocompare.com/documentation?key=Other&cat=allCoinsWithContentEndpoint"));
	}

	private void validatePriceMultiArgs(String fsyms, String tsyms) {
		if (null == fsyms || fsyms.isBlank() || fsyms.length() > MAX_FSYMS_LENGTH)
			throw new IllegalArgumentException(String.format(
					"argument 'fsyms' is required, and cannot exceed a length of {} characters. See {} for details.",
					MAX_FSYMS_LENGTH,
					"https://min-api.cryptocompare.com/documentation?key=Price&cat=multipleSymbolsPriceEndpoint"));
		if (null == tsyms || tsyms.isBlank() || tsyms.length() > MAX_TSYMS_LENGTH)
			throw new IllegalArgumentException(String.format(
					"argument 'tsyms' is required, and cannot exceed a length of {} characters. See {} for details.",
					MAX_TSYMS_LENGTH,
					"https://min-api.cryptocompare.com/documentation?key=Price&cat=multipleSymbolsPriceEndpoint"));
	}

	/**
	 * Helper method to splice the symbols of crypto currencies into query parameter
	 * eligible comma separated list of values, that are no longer than 300
	 * characters.
	 *
	 * @param data   the given map of crypto coins codes to crypto coin data.
	 * @param maxlen the maximum lenght of {@code fsyms} query parameter string (300
	 *               by API notes at
	 *               {@linkplain https://min-api.cryptocompare.com/documentation?key=Price&cat=multipleSymbolsPriceEndpoint})
	 * @return list of strings, each is a comma separated value of strings eligible
	 *         to be {@code fsyms} query parameter.
	 * @throws IllegalArgumentException if {@code maxlen} is greater than 300.
	 */
	private List<String> getSymbolsCSVInternal(Map<String, CryptoCoin> data, int maxlen)
			throws IllegalArgumentException {
		if (maxlen > MAX_FSYMS_LENGTH)
			throw new IllegalArgumentException(String.format(
					"argument 'maxlen' cannot exceed a length of {} characters. See {} for details.", MAX_FSYMS_LENGTH,
					"https://min-api.cryptocompare.com/documentation?key=Price&cat=multipleSymbolsPriceEndpoint"));
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
