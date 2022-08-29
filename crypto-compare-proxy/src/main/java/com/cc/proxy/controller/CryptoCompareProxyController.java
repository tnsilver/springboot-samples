package com.cc.proxy.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import com.cc.proxy.model.CacheTTLRequest;
import com.cc.proxy.model.CryptoCoin;
import com.cc.proxy.model.ProxyResponse;
import com.cc.proxy.service.InMemoryCache;

import lombok.extern.slf4j.Slf4j;
/**
 * The class CryptoCompareProxyController is a REST controller mapped to the
 * root context path {@code "/"}. It handles this application REST API.
 */
@RestController
@RequestMapping("/")
@Slf4j
public class CryptoCompareProxyController {

	// @formatter:off
	@Autowired private InMemoryCache<String, CryptoCoin> cache;
	@Autowired public WebRequest request;
	// @formatter:on

	// @formatter:off
	@RequestMapping(value = "/coins", produces = { APPLICATION_JSON_VALUE }, method = GET)
	public ResponseEntity<ProxyResponse<String[]>> getCryptoCoinSymbols(@RequestParam(required = false) String algorithm, @RequestParam(required = false) String symbol) {
		ProxyResponse<String[]> result = new ProxyResponse<>();
		HttpStatus status = null;
		String response = "", message = "";
		String[] data = null;
		try {
			data = filterCryptoCoinSymbols(symbol, algorithm).stream().toArray(String[]::new);
			String criteria = null == algorithm || algorithm.isBlank() ? null == symbol || symbol.isBlank() ? "all coins" : "symbol" : "algorithm";
			String param = null == algorithm || algorithm.isBlank() ? null == symbol || symbol.isBlank() ? "all coins" : symbol : algorithm;
			int length = null != data ? data.length : 0;
			message = String.format("%d coins matching %s '%s'", length, criteria, param);
			status = length == 0 ? NOT_FOUND : OK;
			response = status.value() + " " + status.getReasonPhrase();
		} catch (Exception ex) {
			log.error("{}", ex.getMessage(), ex);
			status = INTERNAL_SERVER_ERROR;
			response = INTERNAL_SERVER_ERROR.value() + " " + INTERNAL_SERVER_ERROR.getReasonPhrase();
			message = ex.getMessage();
		}
		result.setResponse(response);
		result.setMessage(message);
		result.setData(data);
		return new ResponseEntity<>(result, status);
	}
	// @formatter:on

	// @formatter:off
	@RequestMapping(value = "/coins/data", produces = { APPLICATION_JSON_VALUE }, method = GET)
	public ResponseEntity<ProxyResponse<CryptoCoin[]>> getCryptoCoinsData() {
		ProxyResponse<CryptoCoin[]> result = new ProxyResponse<>();
		CryptoCoin[] data = null;
		HttpStatus status = null;
		String response = "", message = "";
		try {
			data = cache.entrySet().stream().map(e -> e.getValue()).toArray(CryptoCoin[]::new);
			int length = null != data ? data.length : 0;
			message = String.format("%d coins found", length);
			status = length == 0 ? NOT_FOUND : OK;
			response = status.value() + " " + status.getReasonPhrase();
		} catch (Exception ex) {
			log.error("{}", ex.getMessage(), ex);
			status = INTERNAL_SERVER_ERROR;
			response = INTERNAL_SERVER_ERROR.value() + " " + INTERNAL_SERVER_ERROR.getReasonPhrase();
			message = ex.getMessage();
		}
		result.setResponse(response);
		result.setMessage(message);
		result.setData(data);
		return new ResponseEntity<>(result, status);
	}
	// @formatter:on

	// @formatter:off
	@RequestMapping(value = "/coins/{symbol}", produces = { APPLICATION_JSON_VALUE }, method = GET)
	public ResponseEntity<ProxyResponse<CryptoCoin[]>> getCryptoCoinData(@PathVariable(name = "symbol") String symbol) {
		HttpStatus status = null;
		String response = "", message = "";
		ProxyResponse<CryptoCoin[]> result = new ProxyResponse<>();
		CryptoCoin[] data = null;
		try {
			CryptoCoin coin = cache.get(symbol);
			data = (null != coin ? new CryptoCoin[] {coin} : new CryptoCoin[] {});
			int length = (null != data ? data.length : 0);
			message = String.format("%d coin matching symbol '%s' found", length, symbol);
			status = length == 0 ? NOT_FOUND : OK;
			response = status.value() + " " + status.getReasonPhrase();
		} catch (Exception ex) {
			if (ex instanceof RestClientException)
				log.warn("No data found for currency symbol '{}'", symbol);
			else
				log.error("{}",ex.getMessage(), ex);
			status = INTERNAL_SERVER_ERROR;
			response = INTERNAL_SERVER_ERROR.value() + " " + INTERNAL_SERVER_ERROR.getReasonPhrase();
			message = ex.getMessage();
		}
		result.setResponse(response);
		result.setMessage(message);
		result.setData(data);
		return new ResponseEntity<>(result, status);
	}
	// @formatter:on

	@PreAuthorize("@headerAuthorizationService.authorize(#request)")
	@RequestMapping(value = "/cachettl", produces = { APPLICATION_JSON_VALUE }, consumes = { APPLICATION_JSON_VALUE }, method = POST)
	public ResponseEntity<ProxyResponse<Map<String, Long>>> modifyCacheTtl(@RequestBody CacheTTLRequest payload, @Autowired WebRequest request) {
		HttpStatus status = CREATED;
		String response = "", message = "";
		ProxyResponse<Map<String, Long>> result = new ProxyResponse<>();
		Map<String, Long> data = new LinkedHashMap<>();
		try {
			Long oldValue = cache.getTtl();
			Long newValue = payload.getTtl();
			cache.setTtl(newValue);
			data.put("oldValue", oldValue);
			data.put("newValue", newValue);
			response = CREATED.value() + " " + CREATED.getReasonPhrase();
			message = String.format("cache ttl %d ms", newValue);
		} catch (Exception ex) {
			log.error("{}", ex.getMessage(), ex);
			status = INTERNAL_SERVER_ERROR;
			response = INTERNAL_SERVER_ERROR.value() + " " + INTERNAL_SERVER_ERROR.getReasonPhrase();
			message = ex.getMessage();
		}
		result.setResponse(response);
		result.setMessage(message);
		result.setData(data);
		return new ResponseEntity<>(result, status);
	}

	// @formatter:off
	private Set<String> filterCryptoCoinSymbols(String symbol, String algorithm) throws Exception {
		if (null != algorithm && !algorithm.isBlank()) {
			return cache.entrySet().stream()
					.filter(e -> null != e.getValue().getAlgorithm())
					.filter(e -> e.getValue().getAlgorithm().toUpperCase().equals(algorithm.toUpperCase()))
					.map(e -> e.getKey())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		} else if (null != symbol && !symbol.isBlank()) {
			List<String> symbols = Arrays.asList(symbol.split(","));
			return cache.entrySet().stream()
					.filter(e -> null != e.getValue().getSymbol())
					.filter(e -> symbols.stream().map(String::toUpperCase).anyMatch(s -> s.equals(e.getValue().getSymbol().toUpperCase())))
					.map(e -> e.getKey())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		} else {
			return cache.keySet();
		}
	}
	// @formatter:on
}
