package com.cc.proxy.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Internal container for JSON currency rate results from api endpoint
 * {@linkplain https://min-api.cryptocompare.com/data/pricemulti}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Rate {

	// @formatter:off
	@JsonProperty("Response") private String response;
	@JsonProperty("Message") private String message;
	@JsonProperty("HasWarning") Boolean hasWarning;
	@JsonProperty("Type") Integer type;
	// @formatter:on

	/**
	 * The key in this map is a currency code (i.e. 'USD', 'GBP', 'EUR' etc...) and
	 * the value is the currency rate.
	 */
	Map<String, BigDecimal> details = new HashMap<>();

	@JsonProperty("Error") private String error;

	@JsonAnySetter
	public void setDetail(String key, BigDecimal value) {
		details.put(key, value);
	}

	/**
	 * convenience method to retrieve rate by currency code.
	 *
	 * @param code the currency code (e.g. 'USD', 'GBP', 'EUR' etc...)
	 * @return a possibly empty Optional containing the rate of the crypto currency
	 *         obtained by currency code.
	 */
	public Optional<BigDecimal> getRate(String code) {
		return Optional.ofNullable(details.get(code));
	}

}
